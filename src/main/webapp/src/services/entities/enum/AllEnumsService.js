import FetchWrapper from '../../util/FetchWrapper.js';

class AllEnumsService {
  query() {
    return FetchWrapper.getJson('api/enums/all');
  }
}

export default new AllEnumsService();
