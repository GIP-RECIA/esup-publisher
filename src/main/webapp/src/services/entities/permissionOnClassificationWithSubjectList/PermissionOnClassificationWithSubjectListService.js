import DateUtils from '../../util/DateUtils.js'
import FetchWrapper from '../../util/FetchWrapper.js'

class PermissionOnClassificationWithSubjectListService {
  query() {
    return FetchWrapper.getJson('api/permissionOnClassificationWithSubjectLists')
  }

  get(id) {
    return FetchWrapper.getJson(`api/permissionOnClassificationWithSubjectLists/${id}`).then((response) => {
      if (response.data) {
        response.data.createdDate = DateUtils.convertDateTimeFromServer(response.data.createdDate)
        response.data.lastModifiedDate = DateUtils.convertDateTimeFromServer(response.data.lastModifiedDate)
      }
      return response
    })
  }

  update(permissionOnContext) {
    return FetchWrapper.putJson('api/permissionOnClassificationWithSubjectLists', permissionOnContext)
  }

  delete(id) {
    return FetchWrapper.deleteJson(`api/permissionOnClassificationWithSubjectLists/${id}`)
  }

  queryForCtx(ctxType, ctxId) {
    return FetchWrapper.getJson(`api/permissionOnClassificationWithSubjectLists/${ctxType}/${ctxId}`)
  }
}

export default new PermissionOnClassificationWithSubjectListService()
