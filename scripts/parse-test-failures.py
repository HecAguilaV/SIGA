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
        print("No test failures found.")
        with open(summary_file, "a") as f:
            f.write("## Test Results\n")
            f.write("No failed tests found.\n")
        return 0

    with open(summary_file, "a") as f:
        f.write("## ❌ Failed Tests\n\n")
        for xml_path in failed_xmls:
            f.write(f"### {xml_path}\n\n")
            tree = ET.parse(xml_path)
            for tc in tree.findall(".//testcase"):
                failure = tc.find("failure")
                error = tc.find("error")
                if failure is not None:
                    msg = failure.get("message", "no message")
                    text = (failure.text or "")[:500]
                    f.write(f"- ❌ **{tc.get('classname')}.{tc.get('name')}**\n")
                    f.write(f"  _{msg}_\n")
                    if text.strip():
                        f.write(f"  ```\n  {text}\n  ```\n")
                    f.write("\n")
                elif error is not None:
                    msg = error.get("message", "no message")
                    text = (error.text or "")[:500]
                    f.write(f"- ⚠️ **{tc.get('classname')}.{tc.get('name')}**\n")
                    f.write(f"  _{msg}_\n")
                    if text.strip():
                        f.write(f"  ```\n  {text}\n  ```\n")
                    f.write("\n")
    return 0

if __name__ == "__main__":
    sys.exit(main())
