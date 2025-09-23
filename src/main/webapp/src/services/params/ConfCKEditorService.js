import FetchWrapper from '../util/FetchWrapper.js';

class ConfCKEditorService {
  query() {
    return FetchWrapper.getJson('api/conf/ckeditor');
  }
}

export default new ConfCKEditorService();
