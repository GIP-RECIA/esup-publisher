import { flushPromises } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import InsertFilePlugin from '@/components/richtext/InsertFilePlugin.js'
import UploadUtils from '@/services/util/UploadUtils.js'

vi.mock('@/services/util/UploadUtils.js')

describe('insert file plugin', () => {
  it('inserts a secure link for a dropped non-image file', async () => {
    UploadUtils.getCssFileFromType.mockReturnValue('fas fa-file-pdf fa-lg')
    const inserted = []
    const editor = {
      data: { processor: { toView: vi.fn(content => content) }, toModel: vi.fn(view => view) },
      model: { document: { selection: {} }, insertContent: vi.fn(content => inserted.push(content)) },
    }
    const plugin = new InsertFilePlugin(editor)
    plugin.createUploadAdapter = vi.fn(() => ({ upload: () => Promise.resolve({ default: 'https://publisher.example/view/file/document.pdf' }) }))

    plugin.insert(new File(['content'], 'document.pdf', { type: 'application/pdf' }), editor)
    await flushPromises()

    const content = editor.data.processor.toView.mock.calls[0][0]
    expect(content).toContain('<a href="https://publisher.example/view/file/document.pdf" target="_blank" rel="noopener noreferrer">')
    expect(content).toContain('<i class="fas fa-file-pdf fa-lg" aria-hidden="true">&nbsp;</i>')
    expect(content).toContain('<span>document.pdf</span>')
    expect(content).toContain('</a>')
    expect(content).not.toContain('noopener noreferrer"/>')
    expect(inserted).toHaveLength(1)
  })

  it('does not insert content when the upload fails', async () => {
    const editor = {
      data: { processor: { toView: vi.fn() }, toModel: vi.fn() },
      model: { document: { selection: {} }, insertContent: vi.fn() },
    }
    const plugin = new InsertFilePlugin(editor)
    plugin.createUploadAdapter = () => ({ upload: () => Promise.reject(new Error('upload failed')) })

    plugin.insert(new File(['content'], 'document.pdf', { type: 'application/pdf' }), editor)
    await flushPromises()

    expect(editor.model.insertContent).not.toHaveBeenCalled()
  })
})
