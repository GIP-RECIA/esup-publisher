import { flushPromises, shallowMount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import RichText from '@/components/richtext/RichText.vue'
import ConfCKEditorService from '@/services/params/ConfCKEditorService.js'
import ConfFileSizeService from '@/services/params/ConfFileSizeService.js'
import ConfigurationService from '@/services/params/ConfigurationService.js'
import ConfImageSizeService from '@/services/params/ConfImageSizeService.js'
import ConfMimeTypesService from '@/services/params/ConfMimeTypesService.js'

describe('richText.vue tests', () => {
  const provide = {
    publisher: null,
    linkedFilesToContent: [],
    setLinkedFilesToContent: vi.fn(),
  }

  it('initializes the editor data and emits changes', async () => {
    ConfCKEditorService.query = vi.fn().mockReturnValue(Promise.resolve({ data: { value: { mediaUrlPattern: '/^$/' } } }))
    ConfFileSizeService.query = vi.fn().mockReturnValue(Promise.resolve({ data: { value: 1000000 } }))
    ConfImageSizeService.query = vi.fn().mockReturnValue(Promise.resolve({ data: { value: 1000000 } }))
    ConfMimeTypesService.query = vi.fn().mockReturnValue(Promise.resolve({ data: { value: [] } }))
    ConfigurationService.confCKEditor = { mediaUrlPattern: '/^$/' }

    const wrapper = shallowMount(RichText, {
      global: {
        stubs: { Ckeditor: { template: '<div class="ckeditor-stub"></div>' } },
        provide,
      },
      props: { modelValue: '<p>test</p>' },
    })

    await flushPromises()
    expect(wrapper.find('.ckeditor-stub').exists()).toBe(true)
    expect(wrapper.vm.editorData).toStrictEqual('<p>test</p>')
    wrapper.vm.editorData = '<p>new value</p>'
    await flushPromises()
    expect(wrapper.emitted('update:modelValue')).toEqual([['<p>new value</p>']])
  })

  it('configures the supported toolbar and POD provider', () => {
    ConfigurationService.confCKEditor = { mediaUrlPattern: '/^(?:(?:https?:)?\\/\\/)?(pod\\.univ\\.fr\\/video|.*\\.fr\\/POD\\/video)\\/(.*)\\/(\\?is_iframe=true)?$/' }
    const wrapper = shallowMount(RichText, {
      global: {
        stubs: { Ckeditor: { template: '<div class="ckeditor-stub"></div>' } },
        provide,
      },
    })

    const { editorConfig } = wrapper.vm
    const pluginNames = editorConfig.plugins.map(plugin => plugin.pluginName || plugin.name)
    expect(pluginNames).toEqual(expect.arrayContaining(['Essentials', 'Bold', 'Italic', 'ImageUpload', 'MediaEmbed', 'GeneralHtmlSupport', 'IconEditingPlugin', 'InsertFilePlugin']))
    expect(editorConfig.toolbar.items).toEqual(expect.arrayContaining(['heading', 'sourceEditing', 'imageInsert', 'mediaEmbed', 'link']))
    const podProvider = editorConfig.mediaEmbed.extraProviders[0]
    expect(podProvider.name).toBe('POD')
    expect(podProvider.url.test('https://pod.univ.fr/video/demo/')).toBe(true)
    expect(podProvider.html(podProvider.url.exec('https://pod.univ.fr/video/demo/'))).toContain('src="https://pod.univ.fr/video/demo/?is_iframe=true"')
  })
})
