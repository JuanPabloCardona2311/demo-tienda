$p = 'C:\Users\Juan Pablo\OneDrive - elpoli.edu.co\Trabajos U\Proyecto de Construccion de sw\demo\Informe Analisis  (1).docx'
$out = 'C:\Users\Juan Pablo\OneDrive - elpoli.edu.co\Trabajos U\Proyecto de Construccion de sw\demo\InformeAnalisis.txt'
$wd = New-Object -ComObject Word.Application
$wd.Visible = $false
$doc = $wd.Documents.Open($p, $false, $true)
$texto = $doc.Content.Text
$doc.Close()
$wd.Quit()
Set-Content -Path $out -Value $texto -Encoding UTF8
Write-Output "WROTE:$out"
