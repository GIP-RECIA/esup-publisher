import CustomUploadAdapter from "@/components/richtext/CustomUploadAdapter";
import UploadUtils from "@/services/util/UploadUtils";
import ConfigurationService from "@/services/params/ConfigurationService";

jest.mock("@/services/util/UploadUtils");
jest.mock("compressorjs", () =>
  jest.fn((file, options) => options.success(new global.Blob(["x"], { type: file.type }))),
);

describe("CustomUploadAdapter", () => {
  const originalGetImageSize = ConfigurationService.getConfUploadImageSize;

  beforeEach(() => {
    jest.clearAllMocks();
    process.env.VUE_APP_BACK_BASE_URL = "https://publisher.example/";
    ConfigurationService.getConfUploadImageSize = jest.fn().mockReturnValue(1000000);
  });

  afterEach(() => {
    ConfigurationService.getConfUploadImageSize = originalGetImageSize;
  });

  it("uploads a file and returns the decoded backend URL", async () => {
    const file = new File(["content"], "document.pdf", { type: "application/pdf" });
    const request = { abort: jest.fn() };
    UploadUtils.upload.mockImplementation((url, data, success) => {
      success(null, { location: "view%2Ffile%2Fdocument.pdf" });
      return request;
    });
    const successCallback = jest.fn();
    const adapter = new CustomUploadAdapter(
      { file: Promise.resolve(file) },
      42,
      1000,
      "too large",
      successCallback,
    );

    await expect(adapter.upload()).resolves.toEqual({
      default: "https://publisher.example/view/file/document.pdf",
    });
    expect(UploadUtils.upload).toHaveBeenCalledWith(
      "app/upload/",
      expect.objectContaining({
        file,
        entityId: 42,
        isPublic: false,
        name: "document.pdf",
      }),
      expect.any(Function),
      expect.any(Function),
      expect.any(Function),
    );
    expect(successCallback).toHaveBeenCalledWith(file, "view/file/document.pdf");
  });

  it.each([
    ["image/png", true],
    ["audio/mpeg", true],
    ["video/mp4", true],
    ["application/pdf", false],
  ])("marks %s as public=%s", async (type, isPublic) => {
    const file = new File(["content"], "file.bin", { type });
    UploadUtils.upload.mockImplementation((url, data, success) => {
      success(null, { location: "files/file.bin" });
      return { abort: jest.fn() };
    });
    const adapter = new CustomUploadAdapter({ file: Promise.resolve(file) }, 42);

    await adapter.upload();

    expect(UploadUtils.upload.mock.calls[0][1].isPublic).toBe(isPublic);
  });

  it("rejects an oversized file and does not upload it", async () => {
    const file = new File(["content"], "document.pdf", { type: "application/pdf" });
    const errorCallback = jest.fn();
    const adapter = new CustomUploadAdapter(
      { file: Promise.resolve(file) },
      42,
      1,
      "too large",
      null,
      errorCallback,
    );

    await expect(adapter.upload()).rejects.toBeUndefined();
    expect(UploadUtils.upload).not.toHaveBeenCalled();
    expect(errorCallback).toHaveBeenCalledWith("too large");
  });

  it("compresses an oversized image before uploading", async () => {
    const file = new File(["large image"], "photo.png", { type: "image/png" });
    const compressedFile = new File(["x"], "photo.png", { type: "image/png" });
    const adapter = new CustomUploadAdapter({ file: Promise.resolve(file) }, 42, 1000);
    adapter.compressImage = jest.fn().mockResolvedValue(compressedFile);
    UploadUtils.upload.mockImplementation((url, data, success) => {
      success(null, { location: "files/photo.png" });
      return { abort: jest.fn() };
    });

    await adapter.upload();

    expect(adapter.compressImage).toHaveBeenCalledWith(file);
    expect(UploadUtils.upload.mock.calls[0][1].file).toBe(compressedFile);
  });

  it("calls the abort callback and aborts the request", () => {
    const abortCallback = jest.fn();
    const request = { abort: jest.fn() };
    const adapter = new CustomUploadAdapter({ file: Promise.resolve(new File([], "file.txt")) }, 42, null, null, null, null, null, abortCallback);
    adapter.xhr = request;

    adapter.abort();

    expect(abortCallback).toHaveBeenCalledTimes(1);
    expect(request.abort).toHaveBeenCalledTimes(1);
  });
});
