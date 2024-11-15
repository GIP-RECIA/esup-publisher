import FetchWrapper from '../util/FetchWrapper.js'

class MonitoringService {
  getMetrics() {
    return FetchWrapper.getJson('management/jhimetrics')
  }

  checkHealth() {
    return FetchWrapper.getJson('management/health')
  }

  threadDump() {
    return FetchWrapper.getJson('management/threaddump')
  }
}

export default new MonitoringService()
