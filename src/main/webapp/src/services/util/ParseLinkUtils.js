import CommonUtils from './CommonUtils.js';

class ParseLinkUtils {
  parse(header) {
    if (!header || !header.length || header.length === 0) {
      return;
    }
    // Split parts by comma
    let parts = header.split(',');
    let links = {};
    // Parse each part into a named link
    parts.forEach((p) => {
      let section = p.split(';');
      if (section.length !== 2) {
        throw new Error("section could not be split on ';'");
      }
      let url = section[0].replace(/<(.*)>/, '$1').trim();
      let queryString = {};
      url.replace(new RegExp('([^?=&]+)(=([^&]*))?', 'g'), function ($0, $1, $2, $3) {
        queryString[$1] = $3;
      });
      let page = queryString.page;
      if (CommonUtils.isString(page)) {
        page = parseInt(page);
      }
      let name = section[1].replace(/rel="(.*)"/, '$1').trim();
      links[name] = page;
    });

    return links;
  }
}
export default new ParseLinkUtils();
