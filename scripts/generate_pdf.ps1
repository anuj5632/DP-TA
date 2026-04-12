param(
    [string]$InputMarkdown = "PROJECT_REPORT.md",
    [string]$OutputPdf = "PROJECT_REPORT.pdf",
    [string]$TempHtml = "PROJECT_REPORT_temp.html"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Escape-Html {
    param([string]$Text)
    if ($null -eq $Text) { return "" }
    return ($Text.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;"))
}

function Convert-MarkdownToHtml {
    param([string[]]$Lines)

    $sb = New-Object System.Text.StringBuilder

    [void]$sb.AppendLine("<!doctype html>")
    [void]$sb.AppendLine("<html><head><meta charset='utf-8'>")
    [void]$sb.AppendLine("<style>")
    [void]$sb.AppendLine("body { font-family: 'Segoe UI', Tahoma, Arial, sans-serif; margin: 38px; color: #1f2937; line-height: 1.45; }")
    [void]$sb.AppendLine("h1, h2, h3, h4 { color: #0f172a; margin-top: 24px; margin-bottom: 10px; }")
    [void]$sb.AppendLine("h1 { font-size: 30px; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }")
    [void]$sb.AppendLine("h2 { font-size: 22px; border-bottom: 1px solid #e5e7eb; padding-bottom: 4px; }")
    [void]$sb.AppendLine("h3 { font-size: 18px; }")
    [void]$sb.AppendLine("p { margin: 6px 0 10px 0; }")
    [void]$sb.AppendLine("ul, ol { margin: 0 0 10px 22px; }")
    [void]$sb.AppendLine("li { margin: 2px 0; }")
    [void]$sb.AppendLine("code { background: #f3f4f6; padding: 1px 4px; border-radius: 3px; font-family: Consolas, monospace; }")
    [void]$sb.AppendLine("pre { background: #111827; color: #f9fafb; padding: 12px; border-radius: 6px; overflow-x: auto; }")
    [void]$sb.AppendLine("pre code { background: transparent; padding: 0; color: inherit; }")
    [void]$sb.AppendLine(".page-break { page-break-before: always; }")
    [void]$sb.AppendLine("</style></head><body>")

    $inCodeBlock = $false
    $inUl = $false
    $inOl = $false

    foreach ($raw in $Lines) {
        $line = $raw

        if ($line -match '^```') {
            if (-not $inCodeBlock) {
                if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
                if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
                [void]$sb.AppendLine("<pre><code>")
                $inCodeBlock = $true
            } else {
                [void]$sb.AppendLine("</code></pre>")
                $inCodeBlock = $false
            }
            continue
        }

        if ($inCodeBlock) {
            [void]$sb.AppendLine((Escape-Html $line))
            continue
        }

        if ([string]::IsNullOrWhiteSpace($line)) {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            continue
        }

        if ($line -match '^####\s+(.*)$') {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            [void]$sb.AppendLine("<h4>" + (Escape-Html $Matches[1]) + "</h4>")
            continue
        }
        if ($line -match '^###\s+(.*)$') {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            [void]$sb.AppendLine("<h3>" + (Escape-Html $Matches[1]) + "</h3>")
            continue
        }
        if ($line -match '^##\s+(.*)$') {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            [void]$sb.AppendLine("<h2>" + (Escape-Html $Matches[1]) + "</h2>")
            continue
        }
        if ($line -match '^#\s+(.*)$') {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            [void]$sb.AppendLine("<h1>" + (Escape-Html $Matches[1]) + "</h1>")
            continue
        }

        if ($line -match '^\-\s+(.*)$') {
            if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }
            if (-not $inUl) { [void]$sb.AppendLine("<ul>"); $inUl = $true }
            [void]$sb.AppendLine("<li>" + (Escape-Html $Matches[1]) + "</li>")
            continue
        }

        if ($line -match '^\d+\.\s+(.*)$') {
            if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
            if (-not $inOl) { [void]$sb.AppendLine("<ol>"); $inOl = $true }
            [void]$sb.AppendLine("<li>" + (Escape-Html $Matches[1]) + "</li>")
            continue
        }

        if ($inUl) { [void]$sb.AppendLine("</ul>"); $inUl = $false }
        if ($inOl) { [void]$sb.AppendLine("</ol>"); $inOl = $false }

        $escaped = Escape-Html $line
        $escaped = [System.Text.RegularExpressions.Regex]::Replace($escaped, '`([^`]+)`', '<code>$1</code>')
        [void]$sb.AppendLine("<p>$escaped</p>")
    }

    if ($inUl) { [void]$sb.AppendLine("</ul>") }
    if ($inOl) { [void]$sb.AppendLine("</ol>") }
    if ($inCodeBlock) { [void]$sb.AppendLine("</code></pre>") }

    [void]$sb.AppendLine("</body></html>")
    return $sb.ToString()
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$inputPath = Join-Path $projectRoot $InputMarkdown
$pdfPath = Join-Path $projectRoot $OutputPdf
$htmlPath = Join-Path $projectRoot $TempHtml

if (-not (Test-Path $inputPath)) {
    throw "Input markdown file not found: $inputPath"
}

$lines = Get-Content -Path $inputPath
$html = Convert-MarkdownToHtml -Lines $lines
Set-Content -Path $htmlPath -Value $html -Encoding UTF8

$chromeCandidates = @(
    "C:\Program Files\Google\Chrome\Application\chrome.exe",
    "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
    "C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
)

$browser = $null
foreach ($candidate in $chromeCandidates) {
    if (Test-Path $candidate) {
        $browser = $candidate
        break
    }
}

if ($null -eq $browser) {
    throw "No Chrome/Edge executable found for headless PDF generation"
}

$htmlUri = "file:///" + (($htmlPath -replace '\\', '/'))

& $browser --headless=new --disable-gpu --print-to-pdf="$pdfPath" --no-pdf-header-footer "$htmlUri" | Out-Null

if (-not (Test-Path $pdfPath)) {
    throw "PDF generation failed. Output not found at: $pdfPath"
}

Write-Output "PDF generated successfully: $pdfPath"
Write-Output "Temporary HTML used: $htmlPath"
