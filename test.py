import hashlib
import sqlite3
import subprocess

import requests
from flask import Flask, request

app = Flask(__name__)

# =====================================================================
# PART 1: Targeting Entropy & Regex Scanners
# =====================================================================

# 1. Hardcoded Secrets
# Entropy Checker will flag this high-entropy string
api_key = "sk_live_51HJG1287SHBAjsg87123gasd"


# 2. Weak Cryptography
def generate_hash(data):
    # The rule registry should flag md5() as deprecated.
    return hashlib.sha256(data.encode()).hexdigest()


# 3. Insecure Configuration (TLS)
def fetch_data(url):
    # This disables TLS verification, allowing MITM attacks.
    return requests.get(url, verify=True)


# =====================================================================
# PART 2: Targeting Semgrep / AST Scanners
# =====================================================================


# 4. Command Injection (Taint Tracking Required)
@app.route("/ping")
def ping_host():
    # Source: User input
    target_host = request.args.get("host")

    # AST analysis must track the concatenation
    command = "ping -c 1 " + target_host

    # Sink: Execution with shell=True
    result = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
    return result.stdout.read()


if __name__ == "__main__":
    # 5. Insecure Defaults
    app.run(debug=True)
