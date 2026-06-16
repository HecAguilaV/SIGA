#!/usr/bin/env python3
"""Parse JUnit XML test results and print failed tests to GitHub Step Summary."""
import xml.etree.ElementTree as ET
import sys
import os
import glob

def main():
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY", "/dev/null")
    failed_xmls = []

    for xml_path in glob.glob("**/build/test-results/test/*.xml", recursive=True):
        tree = ET.parse(xml_path)
        root = tree.getroot()
        failures = int(root.get("failures", "0"))
        errors = int(root.get("errors", "0"))
        if failures > 0 or errors > 0:
            failed_xmls.append(xml_path)

    if not failed_xmls:
        print("::notice::No test failures found in XML reports.")
        with open(summary_file, "a") as f:
            f.write("## Test Results\n")
            f.write("No failed tests found.\n")
        return 0

    # Print to both stdout (CI logs) and GITHUB_STEP_SUMMARY (PR summary)
    lines = []
    lines.append("## ❌ Failed Tests")
    lines.append("")
    for xml_path in failed_xmls:
        lines.append(f"### {xml_path}")
        lines.append("")
        tree = ET.parse(xml_path)
        for tc in tree.findall(".//testcase"):
            failure = tc.find("failure")
            error = tc.find("error")
            if failure is not None:
                msg = failure.get("message", "no message")
                text = (failure.text or "")[:1000]
                lines.append(f"- ❌ {tc.get('classname')}.{tc.get('name')}")
                lines.append(f"  Message: {msg}")
                if text.strip():
                    lines.append("  Stack trace (first 1000 chars):")
                    lines.append(f"  {text[:200]}")
                lines.append("")
                # Also print to stdout (visible in CI logs)
                print(f"::error::{tc.get('classname')}.{tc.get('name')}: {msg}")
            elif error is not None:
                msg = error.get("message", "no message")
                text = (error.text or "")[:1000]
                lines.append(f"- ⚠️ {tc.get('classname')}.{tc.get('name')}")
                lines.append(f"  Message: {msg}")
                if text.strip():
                    lines.append("  Stack trace (first 1000 chars):")
                    lines.append(f"  {text[:200]}")
                lines.append("")
                print(f"::warning::{tc.get('classname')}.{tc.get('name')}: {msg}")

    output = "\n".join(lines)
    with open(summary_file, "a") as f:
        f.write(output)
    print(output)
    return 0

if __name__ == "__main__":
    sys.exit(main())
