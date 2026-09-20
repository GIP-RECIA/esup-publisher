const { createHash } = require('node:crypto')
const { readFile, writeFile } = require('node:fs/promises')
const { resolve } = require('node:path')
const process = require('node:process')
const checker = require('license-checker-rseidelsohn')
const clarifications = require('../etc/frontend-license-clarifications.json')

const outputPath = resolve('src/main/webapp/public/NOTICE-frontend.txt')
const checkOnly = process.argv.includes('--check')

function collectPackages() {
  return new Promise((resolvePackages, reject) => {
    checker.init({
      start: '.',
      production: true,
      customFormat: {
        copyright: '',
        licenseText: '',
        licenses: '',
        repository: '',
      },
    }, (error, packages) => {
      if (error)
        reject(error)
      else resolvePackages(packages)
    })
  })
}

function normalize(value) {
  return value.replaceAll('\r\n', '\n').split('\n').map(line => line.trimEnd()).join('\n').trim()
}

function render(packages) {
  const components = Object.entries(packages)
    .map(([name, component]) => ({
      name,
      ...component,
      licenseText: normalize(component.licenseText || clarifications[name]?.licenseText || ''),
    }))
    .sort((left, right) => left.name.localeCompare(right.name))

  const unusedClarifications = Object.keys(clarifications).filter(name => !packages[name])
  if (unusedClarifications.length)
    throw new Error(`Unused license clarifications: ${unusedClarifications.join(', ')}`)

  const missingLicenseData = components.filter(component => !component.licenses || component.licenses.includes('UNKNOWN') || !component.licenseText)
  if (missingLicenseData.length) {
    throw new Error(`Missing license data for: ${missingLicenseData.map(component => component.name).join(', ')}`)
  }

  const licenseTexts = new Map()
  for (const component of components) {
    const id = createHash('sha256').update(component.licenseText).digest('hex').slice(0, 12)
    if (!licenseTexts.has(id))
      licenseTexts.set(id, { licenses: component.licenses, text: component.licenseText })
    component.licenseTextId = id
  }

  const componentLines = components.flatMap((component) => {
    const lines = [`- ${component.name}`, `  License: ${component.licenses}`, `  License text: ${component.licenseTextId}`]
    if (component.repository)
      lines.push(`  Repository: ${component.repository}`)
    if (component.copyright)
      lines.push(`  Copyright: ${component.copyright}`)
    return lines
  })
  const licenseLines = [...licenseTexts.entries()]
    .sort(([left], [right]) => left.localeCompare(right))
    .flatMap(([id, license]) => [`## ${id} (${license.licenses})`, '', license.text, ''])

  return [
    'ESUP Publisher frontend third-party notices',
    '===========================================',
    '',
    'Generated from the installed npm production dependency tree. Do not edit manually.',
    'Regenerate with: npm run notice:frontend',
    '',
    'Components',
    '----------',
    '',
    ...componentLines,
    '',
    'License texts',
    '-------------',
    '',
    ...licenseLines,
  ].join('\n').trimEnd()
}

async function main() {
  const notice = `${render(await collectPackages())}\n`
  if (checkOnly) {
    const currentNotice = await readFile(outputPath, 'utf8')
    if (currentNotice !== notice)
      throw new Error('NOTICE-frontend.txt is outdated. Run npm run notice:frontend.')
    return
  }
  await writeFile(outputPath, notice)
}

main().catch((error) => {
  console.error(error.message)
  process.exitCode = 1
})
