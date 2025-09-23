import FetchWrapper from '../util/FetchWrapper.js';

class ConfigurationService {
  get() {
    return FetchWrapper.getJson('management/configprops').then((response) => {
      let properties = [];
      Object.values(response.data).forEach((data) => {
        properties.push(data);
      });
      return properties;
    });
  }

  getEnv() {
    return FetchWrapper.getJson('management/env');
  }
}

export default new ConfigurationService();
