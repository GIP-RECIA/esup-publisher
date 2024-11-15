import FetchWrapper from '../util/FetchWrapper.js'

class ConfMimeTypesService {
  query() {
    return FetchWrapper.getJson('api/conf/authorizedmimetypes')
  }
}

export default new ConfMimeTypesService()
