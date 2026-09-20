import { expect, test } from '@playwright/test'

test.beforeEach(async ({ page }) => {
  const configuration = {
    uploadimagesize: { value: 1000000 },
    uploadfilesize: { value: 1000000 },
    authorizedmimetypes: { value: [] },
    ckeditor: { value: { mediaUrlPattern: '/^(?:(?:https?:)?\\/\\/)?(pod\\.univ\\.fr\\/video|.*\\.fr\\/POD\\/video)\\/(.*)\\/(\\?is_iframe=true)?$/' } },
    injectedWebComponents: [],
  }
  await page.route('**/publisher/api/conf/**', async (route) => {
    const name = new URL(route.request().url()).pathname.split('/').pop()
    await route.fulfill({ json: configuration[name] })
  })
  await page.route('**/publisher/api/enums/all', async (route) => {
    await route.fulfill({ json: {} })
  })
  await page.goto('__e2e/richtext')
  await page.waitForFunction(() => window.__richTextEditor)
})

test('embeds POD media with the configured provider', async ({ page }) => {
  await page.evaluate(() => window.__richTextEditor.execute('mediaEmbed', 'https://pod.univ.fr/video/demo/'))

  await expect.poll(() => page.getByTestId('editor-data').textContent()).toContain('https://pod.univ.fr/video/demo/?is_iframe=true')
})

test('uploads a dropped private file and records its linked file', async ({ page }) => {
  let requestBody
  await page.route('**/app/upload/', async (route) => {
    requestBody = route.request().postData()
    await route.fulfill({ status: 201, headers: { location: 'view%2Ffile%2Fdocument.pdf' } })
  })

  await page.locator('.ck-editor__editable').evaluate((element) => {
    const transfer = new DataTransfer()
    transfer.items.add(new File(['content'], 'document.pdf', { type: 'application/pdf' }))
    element.dispatchEvent(new DragEvent('drop', { bubbles: true, dataTransfer: transfer }))
  })

  await expect.poll(() => page.getByTestId('editor-data').textContent()).toContain('view/file/document.pdf')
  await expect(page.getByTestId('linked-files')).toContainText('document.pdf')
  expect(requestBody).toContain('name="isPublic"\r\n\r\nfalse')
})

test('uploads images as public resources', async ({ page }) => {
  let requestBody
  await page.route('**/app/upload/', async (route) => {
    requestBody = route.request().postData()
    await route.fulfill({ status: 201, headers: { location: 'files%2Fphoto.png' } })
  })

  const result = await page.evaluate(async () => {
    const editor = window.__richTextEditor
    const adapter = editor.plugins.get('FileRepository').createUploadAdapter({
      file: Promise.resolve(new File(['image'], 'photo.png', { type: 'image/png' })),
    })
    return adapter.upload()
  })

  expect(result.default).toContain('files/photo.png')
  expect(requestBody).toContain('name="isPublic"\r\n\r\ntrue')
})
