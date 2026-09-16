import { shallowMount } from "@vue/test-utils";
import RichText from "@/components/richtext/RichText";
import ConfigurationService from "@/services/params/ConfigurationService";

// Tests unitaires du composant RichText
describe("RichText.vue tests", () => {
  beforeEach(() => {
    ConfigurationService.getConfCKEditor = jest.fn().mockReturnValue({
      mediaUrlPattern: "/^(?:(?:https?:)?\\/\\/)?(pod\\.univ\\.fr\\/video|.*\\.fr\\/POD\\/video)\\/(.*)\\/(\\?is_iframe=true)?$/",
    });
  });

  it("test 1 RichText - editorData", (done) => {
    const wrapper = shallowMount(RichText, {
      global: {
        stubs: {
          Ckeditor: { template: '<div class="ckeditor-stub"></div>' },
        },
        provide: {
          publisher: null,
          linkedFilesToContent: [],
          setLinkedFilesToContent: jest.fn(),
        },
      },
      props: {
        modelValue: "<p>test</p>",
      },
    });
    expect(wrapper.find(".ckeditor-stub").exists()).toBe(true);
    expect(wrapper.vm.editorData).toStrictEqual("<p>test</p>");

    wrapper.vm.editorData = "<p>new value</p>";
    wrapper.vm.$nextTick(() => {
      const updateEvent = wrapper.emitted("update:modelValue");
      expect(updateEvent).toHaveLength(1);
      expect(updateEvent[0]).toEqual(["<p>new value</p>"]);
      done();
    });
  });

  it("configures the supported toolbar and POD provider", () => {
    const wrapper = shallowMount(RichText, {
      global: {
        stubs: {
          Ckeditor: { template: '<div class="ckeditor-stub"></div>' },
        },
        provide: {
          publisher: null,
          linkedFilesToContent: [],
          setLinkedFilesToContent: jest.fn(),
        },
      },
    });

    const { editorConfig } = wrapper.vm;
    const pluginNames = editorConfig.plugins.map((plugin) => plugin.pluginName || plugin.name);

    expect(pluginNames).toEqual(
      expect.arrayContaining([
        "Essentials",
        "Bold",
        "Italic",
        "ImageUpload",
        "MediaEmbed",
        "GeneralHtmlSupport",
        "IconEditingPlugin",
        "InsertFilePlugin",
      ]),
    );
    expect(editorConfig.toolbar.items).toEqual(
      expect.arrayContaining(["heading", "sourceEditing", "imageInsert", "mediaEmbed", "link"]),
    );
    expect(editorConfig.mediaEmbed.extraProviders).toHaveLength(1);
    expect(editorConfig.mediaEmbed.extraProviders[0].name).toBe("POD");
    const podProvider = editorConfig.mediaEmbed.extraProviders[0];
    expect(podProvider.url.test("https://pod.univ.fr/video/demo/")).toBe(true);
    expect(podProvider.html(podProvider.url.exec("https://pod.univ.fr/video/demo/"))).toContain(
      'src="https://pod.univ.fr/video/demo/?is_iframe=true"',
    );
  });

  /* it("test 2 RichText - mediaEmbed", async (done) => {
    const wrapper = shallowMount(RichText);
    const videoUrl = "https://test.fr/POD/video/foo/";

    const { editorState, $nextTick } = wrapper.vm;

    expect(editorState).toBeDefined();
    editorState.execute("mediaEmbed", videoUrl);
    $nextTick(() => {
      const updateEvent = wrapper.emitted("update:modelValue");
      expect(updateEvent).toHaveLength(1);
      expect(updateEvent[0]).toEqual([
        `<figure class="media"><div data-oembed-url="${videoUrl}"><div><iframe src="${videoUrl}?is_iframe=true" style="padding: 0; margin: 0; border:0" allowfullscreen="" width="640" height="360"></iframe></div></div></figure>`,
      ]);
      done();
    });
  }); */
});
