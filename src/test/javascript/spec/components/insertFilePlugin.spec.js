import InsertFilePlugin from "@/components/richtext/InsertFilePlugin";
import UploadUtils from "@/services/util/UploadUtils";
import { flushPromises } from "@vue/test-utils";

jest.mock("@/services/util/UploadUtils");

describe("InsertFilePlugin", () => {
  it("inserts a secure link for a dropped non-image file", async () => {
    UploadUtils.getCssFileFromType.mockReturnValue("fas fa-file-pdf fa-lg");
    const inserted = [];
    const editor = {
      data: {
        processor: { toView: jest.fn((content) => content) },
        toModel: jest.fn((view) => view),
      },
      model: {
        document: { selection: {} },
        insertContent: jest.fn((content) => inserted.push(content)),
      },
    };
    const plugin = new InsertFilePlugin(editor);
    plugin.createUploadAdapter = jest.fn(() => ({
      upload: () => Promise.resolve({ default: "https://publisher.example/view/file/document.pdf" }),
    }));
    const file = new File(["content"], "document.pdf", { type: "application/pdf" });

    plugin.insert(file, editor);
    await flushPromises();

    const content = editor.data.processor.toView.mock.calls[0][0];
    expect(content).toContain('<a href="https://publisher.example/view/file/document.pdf" target="_blank" rel="noopener noreferrer">');
    expect(content).toContain('<i class="fas fa-file-pdf fa-lg" aria-hidden="true">&nbsp;</i>');
    expect(content).toContain("<span>document.pdf</span>");
    expect(content).toContain("</a>");
    expect(content).not.toContain("noopener noreferrer\"/>");
    expect(inserted).toHaveLength(1);
  });

  it("does not insert content when the upload fails", async () => {
    const editor = {
      data: { processor: { toView: jest.fn() }, toModel: jest.fn() },
      model: { document: { selection: {} }, insertContent: jest.fn() },
    };
    const plugin = new InsertFilePlugin(editor);
    plugin.createUploadAdapter = () => ({ upload: () => Promise.reject(new Error("upload failed")) });

    plugin.insert(new File(["content"], "document.pdf", { type: "application/pdf" }), editor);
    await flushPromises();

    expect(editor.model.insertContent).not.toHaveBeenCalled();
  });
});
