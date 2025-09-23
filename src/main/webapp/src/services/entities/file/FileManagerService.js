import FetchWrapper from '../../util/FetchWrapper.js';

class FileManagerService {
  delete(entityId, isPublic, fileUri) {
    return FetchWrapper.deleteJson('api/file/' + entityId + '/' + isPublic + '/' + fileUri);
  }
}

export default new FileManagerService();
