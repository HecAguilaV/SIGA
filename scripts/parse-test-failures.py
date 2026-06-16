#!/usr/bin/env python3
"""Parse JUnit XML test results and display failures in a human-readable format."""

import glob
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def parse_test_results(base_dir: str = ".") -> int:
    """Parse all JUnit XML files and print failures.

    Returns the number of failed tests found.
    """
    xml_files = glob.glob(
        f"{base_dir}/**/build/test-results/**/*.xml", recursive=True
    )

    if not xml_files:
        print("ℹ️  No JUnit XML test result files found.")
        return 0

    total_tests = 0
    total_failures = 0
    total_errors = 0
    total_skipped = 0
    failure_details: list[dict[str, str]] = []

    for xml_file in sorted(xml_files):
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
        except ET.ParseError:
            print(f"⚠️  Could not parse: {xml_file}")
            continue

        # Handle both <testsuite> and <testsuites> root elements
        suites = (
            root.findall("testsuite")
            if root.tag == "testsuites"
            else [root]
        )

        for suite in suites:
            suite_name = suite.get("name", "unknown")
            total_tests += int(suite.get("tests", 0))
            total_failures += int(suite.get("failures", 0))
            total_errors += int(suite.get("errors", 0))
            total_skipped += int(suite.get("skipped", 0))

            for testcase in suite.findall("testcase"):
                test_name = testcase.get("name", "unknown")
                class_name = testcase.get("classname", "unknown")

                failure = testcase.find("failure")
                error = testcase.find("error")
                element = failure if failure is not None else error

                if element is not None:
                    failure_details.append(
                        {
                            "class": class_name,
                            "test": test_name,
                            "type": element.get("type", "unknown"),
                            "message": element.get("message", "No message"),
                            "detail": (element.text or "")[:500],
                        }
                    )

    # Print summary
    print("=" * 70)
    print("📊 Test Results Summary")
    print("=" * 70)
    print(f"  Total tests:  {total_tests}")
    print(f"  ✅ Passed:     {total_tests - total_failures - total_errors - total_skipped}")
    print(f"  ❌ Failures:   {total_failures}")
    print(f"  💥 Errors:     {total_errors}")
    print(f"  ⏭️  Skipped:    {total_skipped}")
    print("=" * 70)

    if not failure_details:
        print("\n✅ All tests passed!")
        return 0

    print(f"\n❌ {len(failure_details)} test(s) failed:\n")

    for i, fail in enumerate(failure_details, 1):
        print(f"─── Failure {i} {'─' * 50}")
        print(f"  Class:   {fail['class']}")
        print(f"  Test:    {fail['test']}")
        print(f"  Type:    {fail['type']}")
        print(f"  Message: {fail['message']}")
        if fail["detail"].strip():
            print(f"  Detail:\n    {fail['detail'].strip()}")
        print()

    return len(failure_details)


if __name__ == "__main__":
    base = sys.argv[1] if len(sys.argv) > 1 else "."
    failures = parse_test_results(base)
    sys.exit(1 if failures > 0 else 0)
