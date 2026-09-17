import { describe, expect, it, vi } from 'vitest'
import IconEditingPlugin from '@/components/richtext/IconEditingPlugin.js'

describe('icon editing plugin', () => {
  it('registers the icon model and conversion rules', () => {
    const schema = { register: vi.fn() }
    const converters = []
    const plugin = Object.create(IconEditingPlugin.prototype)
    plugin.editor = { model: { schema }, conversion: { for: vi.fn(() => ({ elementToElement: vi.fn(converter => converters.push(converter)) })) } }

    plugin.init()

    expect(schema.register).toHaveBeenCalledWith('icon-container', expect.objectContaining({ isInline: true, isObject: true, allowAttributes: ['iconClass'] }))
    expect(converters).toHaveLength(3)
    expect(converters[0].view).toEqual({ name: 'i', classes: [{ key: /^(fa|fas|mdi)$/, value: true }] })
  })

  it('upcasts an icon class into the icon model', () => {
    const converters = []
    const plugin = Object.create(IconEditingPlugin.prototype)
    plugin.editor = { model: { schema: { register: vi.fn() } }, conversion: { for: vi.fn(() => ({ elementToElement: vi.fn(converter => converters.push(converter)) })) } }

    plugin.init()
    const modelElement = converters[0].model({ getAttribute: () => 'fas fa-file-pdf fa-lg' }, { writer: { createElement: vi.fn((name, attributes) => ({ name, attributes })) } })

    expect(modelElement).toEqual({ name: 'icon-container', attributes: { iconClass: 'fas fa-file-pdf fa-lg' } })
  })
})
