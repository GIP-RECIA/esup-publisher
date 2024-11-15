import FetchWrapper from '../util/FetchWrapper.js'

class LogsService {
  findAll() {
    return FetchWrapper.getJson('api/logs')
  }

  changeLevel(name, level) {
    return FetchWrapper.putJson('api/logs', { name, level })
  }
}

export default new LogsService()
