import FetchWrapper from '../util/FetchWrapper.js'

class ConfFileSizeService {
  query() {
    return FetchWrapper.getJson('api/conf/uploadfilesize')
  }
}

export default new ConfFileSizeService()
