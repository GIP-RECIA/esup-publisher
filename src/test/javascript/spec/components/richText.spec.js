import { flushPromises, shallowMount } from '@vue/test-utils';
import RichText from '@/components/richtext/RichText.vue';
import ConfCKEditorService from '@/services/params/ConfCKEditorService.js';
import ConfFileSizeService from '@/services/params/ConfFileSizeService.js';
import ConfImageSizeService from '@/services/params/ConfImageSizeService.js';
import ConfMimeTypesService from '@/services/params/ConfMimeTypesService.js';
import { describe, expect, it, vi } from 'vitest';

// Tests unitaires du composant RichText
describe('RichText.vue tests', () => {
  it('test 1 RichText - editorData', async () => {
    ConfCKEditorService.query = vi.fn().mockReturnValue(Promise.resolve({}));
    ConfFileSizeService.query = vi.fn().mockReturnValue(Promise.resolve({}));
    ConfImageSizeService.query = vi.fn().mockReturnValue(Promise.resolve({}));
    ConfMimeTypesService.query = vi.fn().mockReturnValue(Promise.resolve({}));

    const wrapper = shallowMount(RichText, {
      global: {
        stubs: {
          Ckeditor: { template: '<div class="ckeditor-stub"></div>' },
        },
      },
      props: {
        modelValue: '<p>test</p>',
      },
    });

    await flushPromises();
    expect(wrapper.find('.ckeditor-stub').exists()).toBe(true);
    expect(wrapper.vm.editorData).toStrictEqual('<p>test</p>');
    wrapper.vm.editorData = '<p>new value</p>';
    await flushPromises();
    const updateEvent = wrapper.emitted('update:modelValue');
    expect(updateEvent).toHaveLength(1);
    expect(updateEvent[0]).toEqual(['<p>new value</p>']);
  });

  /* it('test 2 RichText - mediaEmbed', async () => {
    const wrapper = shallowMount(RichText);
    const videoUrl = 'https://test.fr/POD/video/foo/';

    await flushPromises();
    expect(wrapper.vm.editorState).toBeDefined();
    wrapper.vm.editorState.execute('mediaEmbed', videoUrl);
    await flushPromises();
    const updateEvent = wrapper.emitted('update:modelValue');
    expect(updateEvent).toHaveLength(1);
    expect(updateEvent[0]).toEqual([
      `<figure class="media"><div data-oembed-url="${videoUrl}"><div><iframe src="${videoUrl}?is_iframe=true" style="padding: 0; margin: 0; border:0" allowfullscreen="" width="640" height="360"></iframe></div></div></figure>`,
    ]);
  }); */
});
