const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const ROOT_DIR = path.resolve(__dirname, '..');

// 1. Valores por defecto (Fallback) para la base de datos de servicios
const defaultServices = [
  { name: "auth",      fullName: "Auth Service",      type: "backend",  instr: 78, branch: 58, line: 83, totalLine: 836,  reportPath: "services/auth/build/reports/jacoco/test/html/index.html", testPath: "services/auth/build/reports/tests/test/index.html" },
  { name: "agent",     fullName: "Agent Service",     type: "backend",  instr: 82, branch: 48, line: 87, totalLine: 619,  reportPath: "services/agent/build/reports/jacoco/test/html/index.html", testPath: "services/agent/build/reports/tests/test/index.html" },
  { name: "billing",   fullName: "Billing Service",   type: "backend",  instr: 10, branch: 0,  line: 10, totalLine: 630,  reportPath: "services/billing/build/reports/jacoco/test/html/index.html", testPath: "services/billing/build/reports/tests/test/index.html" },
  { name: "inventory", fullName: "Inventory Service", type: "backend",  instr: 88, branch: 63, line: 93, totalLine: 1003, reportPath: "services/inventory/build/reports/jacoco/test/html/index.html", testPath: "services/inventory/build/reports/tests/test/index.html" },
  { name: "sales",     fullName: "Sales Service",     type: "backend",  instr: 75, branch: 43, line: 81, totalLine: 578,  reportPath: "services/sales/build/reports/jacoco/test/html/index.html", testPath: "services/sales/build/reports/tests/test/index.html" },
  { name: "dashboard", fullName: "Frontend Dashboard", type: "frontend", instr: 0,  branch: 0,  line: 0,  totalLine: 0,    reportPath: "apps/dashboard/coverage/index.html", testPath: "apps/dashboard/coverage/index.html" }
];

// Helper para parsear JaCoCo XML si existe
function parseJacocoXml(serviceName) {
  const xmlPath = path.join(ROOT_DIR, 'services', serviceName, 'build/reports/jacoco/test/jacocoTestReport.xml');
  try {
    if (!fs.existsSync(xmlPath)) return null;
    const content = fs.readFileSync(xmlPath, 'utf8');
    
    const getPct = (type) => {
      const match = content.match(new RegExp(`<counter type="${type}" missed="(\\d+)" covered="(\\d+)"/>`));
      if (match) {
        const missed = parseInt(match[1], 10);
        const covered = parseInt(match[2], 10);
        const total = missed + covered;
        return total > 0 ? Math.round((covered / total) * 100) : 0;
      }
      return null;
    };

    const getLines = () => {
      const match = content.match(new RegExp(`<counter type="LINE" missed="(\\d+)" covered="(\\d+)"/>`));
      if (match) {
        return parseInt(match[1], 10) + parseInt(match[2], 10);
      }
      return 0;
    };

    const instr = getPct('INSTRUCTION');
    const branch = getPct('BRANCH');
    const line = getPct('LINE');
    const totalLine = getLines();

    if (instr !== null) {
      return { instr, branch: branch ?? 0, line: line ?? 0, totalLine };
    }
  } catch (e) {
    // Silencioso, retorna null
  }
  return null;
}

// Recolectar Cobertura del Frontend (Vitest)
function getFrontendCoverage() {
  const summaryPath = path.join(ROOT_DIR, 'apps/dashboard/coverage/coverage-summary.json');
  try {
    if (fs.existsSync(summaryPath)) {
      const data = JSON.parse(fs.readFileSync(summaryPath, 'utf8'));
      if (data.total) {
        return {
          instr: Math.round(data.total.statements.pct),
          branch: Math.round(data.total.branches.pct),
          line: Math.round(data.total.lines.pct),
          totalLine: data.total.lines.total
        };
      }
    }
  } catch (e) {
    console.warn("No se pudo leer la cobertura del frontend:", e.message);
  }
  return null;
}

// Obtener estado de los contenedores Docker
function getDockerStatus() {
  const containerNames = [
    { key: 'siga-auth', dockerName: 'siga-auth' },
    { key: 'siga-inventory', dockerName: 'siga-inventory' },
    { key: 'siga-sales', dockerName: 'siga-sales' },
    { key: 'siga-agent', dockerName: 'siga-agent' },
    { key: 'siga-billing', dockerName: 'siga-billing' },
    { key: 'siga-gateway', dockerName: 'siga-gateway' },
    { key: 'siga-registry', dockerName: 'siga-eureka' },
    { key: 'containerflow', dockerName: 'siga-ops' }
  ];
  
  try {
    const stdout = execSync('docker ps --format "{{.Names}}:{{.Status}}"', { encoding: 'utf8', stdio: ['pipe', 'pipe', 'ignore'] });
    const activeContainers = {};
    stdout.split('\n').forEach(line => {
      const [name, status] = line.split(':');
      if (name) {
        // Encontrar coincidencia parcial de nombres
        containerNames.forEach(c => {
          if (name.includes(c.dockerName)) {
            activeContainers[c.key] = status.toLowerCase().includes('up') ? 'running' : 'stopped';
          }
        });
      }
    });

    const containers = containerNames.map(c => ({
      name: c.key,
      status: activeContainers[c.key] || 'offline'
    }));

    return {
      status: 'online',
      containers
    };
  } catch (e) {
    return {
      status: 'offline',
      containers: containerNames.map(c => ({ name: c.key, status: 'offline' }))
    };
  }
}

// Analizar auditorías de seguridad (Semgrep / Gitleaks)
function getSecurityAudits() {
  let semgrepCount = 0;
  let semgrepStatus = 'unknown';
  let semgrepFindings = [];
  let gitleaksCount = 0;
  let gitleaksStatus = 'unknown';
  let gitleaksFindings = [];

  const semgrepReportPath = path.join(ROOT_DIR, 'build/reports/security/semgrep.json');
  const gitleaksReportPath = path.join(ROOT_DIR, 'build/reports/security/gitleaks.json');

  try {
    if (fs.existsSync(semgrepReportPath)) {
      const semgrepData = JSON.parse(fs.readFileSync(semgrepReportPath, 'utf8'));
      semgrepCount = semgrepData.results ? semgrepData.results.length : 0;
      semgrepStatus = semgrepCount === 0 ? 'success' : 'warning';
      if (semgrepData.results) {
        semgrepFindings = semgrepData.results.map(r => ({
          rule: r.check_id,
          path: r.path,
          line: r.start ? r.start.line : 0,
          message: r.extra ? r.extra.message.trim() : '',
          severity: r.extra ? r.extra.severity : 'INFO'
        }));
      }
    } else {
      // Intento rápido de ver si semgrep está configurado, si no reportamos desconocido
      semgrepStatus = 'pending';
    }
  } catch (e) {
    semgrepStatus = 'error';
  }

  try {
    if (fs.existsSync(gitleaksReportPath)) {
      const gitleaksData = JSON.parse(fs.readFileSync(gitleaksReportPath, 'utf8'));
      gitleaksCount = Array.isArray(gitleaksData) ? gitleaksData.length : 0;
      gitleaksStatus = gitleaksCount === 0 ? 'success' : 'danger';
      if (Array.isArray(gitleaksData)) {
        gitleaksFindings = gitleaksData.map(l => {
          let cleanMatch = l.Match || '';
          if (l.Secret && cleanMatch.includes(l.Secret)) {
            cleanMatch = cleanMatch.replace(l.Secret, '[REDACCIONADO]');
          }
          return {
            rule: l.RuleID || l.Description || 'generic',
            path: l.File || '',
            line: l.StartLine || 0,
            match: cleanMatch
          };
        });
      }
    } else {
      gitleaksStatus = 'pending';
    }
  } catch (e) {
    gitleaksStatus = 'error';
  }

  return {
    semgrep: { status: semgrepStatus, count: semgrepCount, lastRun: new Date().toISOString(), findings: semgrepFindings },
    gitleaks: { status: gitleaksStatus, count: gitleaksCount, lastRun: new Date().toISOString(), findings: gitleaksFindings }
  };
}

// Función principal
function main() {
  console.log("Iniciando recopilación de datos DevOps...");

  // 1. Recopilar cobertura para cada servicio
  const services = defaultServices.map(service => {
    if (service.type === 'frontend') {
      const frontCov = getFrontendCoverage();
      if (frontCov) {
        return { ...service, ...frontCov };
      }
    } else {
      const backCov = parseJacocoXml(service.name);
      if (backCov) {
        return { ...service, ...backCov };
      }
    }
    return service; // Retornar fallback si no hay datos nuevos
  });

  // 2. Obtener estado de Docker
  const docker = getDockerStatus();

  // 3. Obtener estado de seguridad
  const security = getSecurityAudits();

  // 4. Armar el objeto JSON DevOps
  const devopsData = {
    services,
    security,
    docker,
    updatedAt: new Date().toLocaleString('es-AR', { timeZone: 'America/Santiago' })
  };

  // 5. Inyectar en coverage.html
  const coverageHtmlPath = path.join(ROOT_DIR, 'coverage.html');
  try {
    if (!fs.existsSync(coverageHtmlPath)) {
      console.error("Error: no se encontró coverage.html en la raíz del proyecto.");
      process.exit(1);
    }

    let html = fs.readFileSync(coverageHtmlPath, 'utf8');

    // Regex para encontrar el script de inyección
    const startTag = '<!-- === DEVOPS DATA START === -->';
    const endTag = '<!-- === DEVOPS DATA END === -->';
    
    const startIdx = html.indexOf(startTag);
    const endIdx = html.indexOf(endTag);

    const dataScriptContent = `\n<script id="devops-data-injected">\nconst devopsData = ${JSON.stringify(devopsData, null, 2)};\n</script>\n`;

    if (startIdx !== -1 && endIdx !== -1) {
      // Reemplazar el contenido entre las etiquetas
      html = html.substring(0, startIdx + startTag.length) + dataScriptContent + html.substring(endIdx);
    } else {
      // Si no existen las etiquetas, buscar <head> o el primer <script> para insertarlas
      console.log("Insertando etiquetas de inyección DevOps por primera vez...");
      const headCloseIdx = html.indexOf('</head>');
      if (headCloseIdx !== -1) {
        const markerBlock = `\n${startTag}${dataScriptContent}${endTag}\n`;
        html = html.substring(0, headCloseIdx) + markerBlock + html.substring(headCloseIdx);
      } else {
        console.error("No se pudo inyectar los datos: estructura HTML no válida.");
        process.exit(1);
      }
    }

    fs.writeFileSync(coverageHtmlPath, html, 'utf8');
    console.log("¡Reporte DevOps unificado generado con éxito en coverage.html!");
  } catch (e) {
    console.error("Error al inyectar datos en coverage.html:", e);
    process.exit(1);
  }
}

main();
