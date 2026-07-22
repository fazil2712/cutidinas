import sys, json
from graphify.build import build_from_json
from graphify.cluster import score_all
from graphify.analyze import god_nodes, surprising_connections, suggest_questions
from graphify.report import generate
from pathlib import Path

extraction = json.loads(Path('graphify-out/.graphify_extract.json').read_text(encoding="utf-8-sig"))
detection  = json.loads(Path('graphify-out/.graphify_detect.json').read_text(encoding="utf-8-sig"))
analysis   = json.loads(Path('graphify-out/.graphify_analysis.json').read_text(encoding="utf-8-sig"))

G = build_from_json(extraction)
communities = {int(k): v for k, v in analysis['communities'].items()}
cohesion = {int(k): v for k, v in analysis['cohesion'].items()}
tokens = {'input': extraction.get('input_tokens', 0), 'output': extraction.get('output_tokens', 0)}

labels = {
    0: "Custom User Details",
    1: "Admin Controller",
    2: "Application Bootstrap",
    3: "Domain Models",
    4: "User Services",
    5: "Thymeleaf Templates",
    6: "Security Configuration",
    7: "Login Controller",
    8: "Data Access",
    9: "Application Tests",
    10: "Cuti Entity",
    11: "Helper Scripts",
    12: "Cuti Repository",
    13: "Pengajuan Cuti Entity",
    14: "User Entity",
    15: "Pengajuan Cuti Repository",
    16: "PowerShell Scripts",
    17: "Bash Scripts",
    18: "Maven Wrapper",
    19: "Main Application",
    20: "Security Config Class",
    21: "Login Controller Class",
    22: "Tests Class",
    23: "Graphify Rule",
    24: "Graphify Workflow",
    25: "Index Template",
    26: "Login Template",
    27: "Company Logo"
}

questions = suggest_questions(G, communities, labels)

report = generate(G, communities, cohesion, labels, analysis['gods'], analysis['surprises'], detection, tokens, '.', suggested_questions=questions)
Path('graphify-out/GRAPH_REPORT.md').write_text(report, encoding="utf-8")
Path('graphify-out/.graphify_labels.json').write_text(json.dumps({str(k): v for k, v in labels.items()}, ensure_ascii=False), encoding="utf-8")
print('Report updated with community labels')
