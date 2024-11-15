import FetchWrapper from '../../util/FetchWrapper.js'

class ContextService {
  query(search) {
    return FetchWrapper.getJson(
      `api/contexts?${
        new URLSearchParams({
          search,
        })}`,
    )
  }
}

export default new ContextService()
