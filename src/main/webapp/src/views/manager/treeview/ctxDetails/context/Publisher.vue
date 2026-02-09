<script>
import { Modal } from 'bootstrap'
import PrincipalService from '@/services/auth/PrincipalService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'
import CategoryForm from './CategoryForm.vue'

export default {
  name: 'Publisher',
  components: {
    CategoryForm,
  },
  inject: [
    'context',
    'editedContext',
    'setEditedContext',
    'updateContext',
    'confirmDeleteContext',
    'confirmUpdateContext',
    'canCreateCategory',
    'canManage',
    'autorizedDisplayOrderTypeList',
    'autorizedPermissionClassList',
    'itemsDisplayOrderTypeList',
    'publisher',
    'getEnumlabel',
    'createContext',
    'appUrl',
  ],
  data() {
    return {
      deleteModal: null,
      updateModal: null,
      categoryForm: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  computed: {
    editedContextDisplayName: {
      get() {
        return this.editedContext.displayName || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.displayName = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextPermissionType: {
      get() {
        return this.editedContext.permissionType || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.permissionType = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextUsed: {
      get() {
        return this.editedContext.used || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.used = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDefaultDisplayOrder: {
      get() {
        return this.editedContext.defaultDisplayOrder || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.defaultDisplayOrder = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDefaultItemsDisplayOrder: {
      get() {
        return this.editedContext.defaultItemsDisplayOrder || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.defaultItemsDisplayOrder = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDisplayOrder: {
      get() {
        return this.editedContext.displayOrder || 0
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.displayOrder = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextHasSubPermsManagement: {
      get() {
        return this.editedContext.hasSubPermsManagement || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.hasSubPermsManagement = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDoHighlight: {
      get() {
        return this.editedContext.doHighlight || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.doHighlight = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    // Nom du contexte du publisher
    contextName() {
      return this.context && this.context.context
        ? `${this.context.context.reader.displayName
        } - ${
          this.context.context.redactor.displayName
        } - ${
          this.context.context.organization.name}`
        : ''
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'editedContext.displayName': function (newVal) {
      this.formValidator.checkTextFieldValidity('displayName', newVal, 3, 50, true)
    },
    'editedContext.permissionType': function (newVal) {
      this.formValidator.checkTextFieldValidity('permissionType', newVal, null, null, true)
    },
    'editedContext.defaultDisplayOrder': function (newVal) {
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', newVal, null, null, true)
    },
    'editedContext.displayOrder': function (newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 9, true)
    },
    'editedContext.defaultItemsDisplayOrder': function (newVal) {
      this.formValidator.checkTextFieldValidity('defaultItemsDisplayOrder', newVal, null, null, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deletePublisherConfirmation)
    this.updateModal = new Modal(this.$refs.savePublisherModal)
    this.categoryForm = this.$refs.categoryForm
  },
  methods: {
    isInRole(role) {
      return PrincipalService.isInRole(role)
    },
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('displayName', this.editedContext.displayName, 3, 50, true)
      this.formValidator.checkTextFieldValidity('permissionType', this.editedContext.permissionType, null, null, true)
      this.formValidator.checkTextFieldValidity('defaultDisplayOrder', this.editedContext.defaultDisplayOrder, null, null, true)
      this.formValidator.checkNumberFieldValidity('displayOrder', this.editedContext.displayOrder, 0, 9, true)
      this.formValidator.checkTextFieldValidity('defaultItemsDisplayOrder', this.editedContext.defaultItemsDisplayOrder, null, null, true)
    },
    // Méthode de création et de mise à jour de contexte de publication
    confirmUpdate() {
      this.confirmUpdateContext('PUBLISHER', () => {
        this.updateModal.hide()
      })
    },
    createCategory() {
      this.createContext('CATEGORY', () => {
        this.categoryForm.show()
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de contexte de publication
    updatePublisher() {
      this.updateContext(() => {
        this.initFormValidator()
        this.updateModal.show()
      })
    },
    // Méthode en charge d'ouvrir la modale de suppression de contexte de publication
    deletePublisher() {
      this.deleteModal.show()
    },
    // Méthode en charge de supprimer un contexte de publication
    confirmDelete() {
      this.confirmDeleteContext(() => {
        this.deleteModal.hide()
      })
    },
    getDisplayOrderTypeLabel(name) {
      return this.getEnumlabel('displayOrderType', name) || ''
    },
    getPermissionClassLabel(name) {
      return this.getEnumlabel('permissionClass', name) || ''
    },
    onChangeDefaultDisplayOrder() {
      // Au changement de type d'ordre d'affichage du contexte, si celui-ce est différent de CUSTOM
      // et que l'ordre d'affichage est invalide, on le passe à 0
      if (this.editedContext.defaultDisplayOrder !== 'CUSTOM' && this.formValidator.hasError('displayOrder')) {
        this.editedContextDisplayOrder = 0
      }
    },
  },
}
</script>

<template>
  <div>
    <h3 class="mt-3 mb-2">
      {{ $t('manager.treeview.details.context.properties') }}
    </h3>
    <div class="mb-3">
      <button v-can-edit="context.contextKey" type="button" class="btn btn-primary me-1" @click="updatePublisher()">
        <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
      </button>
      <button v-has-role="'ROLE_ADMIN'" type="button" class="btn btn-danger me-1" @click="deletePublisher()">
        <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
      </button>
      <button v-if="canCreateCategory" v-can-create-in="context.contextKey" type="button" class="btn btn-primary" @click="createCategory()">
        <span class="fas fa-pencil" />&nbsp;<span>{{ $t('category.home.createLabel') }}</span>
      </button>
    </div>

    <dl v-has-role="'ROLE_ADMIN'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.context.key') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ contextName }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.displayName') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.displayName }}</span>
      </dd>
    </dl>
    <dl v-if="canCreateCategory" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.permissionType') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ $t(getPermissionClassLabel(context.permissionType)) }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.used') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span><input v-model="context.used" type="checkbox" class="input-sm" disabled></span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.defaultDisplayOrder') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ $t(getDisplayOrderTypeLabel(context.defaultDisplayOrder)) }}</span>
      </dd>
    </dl>
    <dl v-has-role="'ROLE_ADMIN'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.displayOrder') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.displayOrder }}</span>
      </dd>
    </dl>
    <dl v-if="publisher.context.redactor.writingMode !== 'STATIC'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.defaultItemsDisplayOrder') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ $t(getDisplayOrderTypeLabel(context.defaultItemsDisplayOrder)) }}</span>
      </dd>
    </dl>
    <dl v-if="canCreateCategory" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.hasSubPermsManagement') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span><input v-model="context.hasSubPermsManagement" type="checkbox" class="input-sm" disabled></span>
      </dd>
    </dl>
    <dl v-has-role="'ROLE_ADMIN'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('publisher.doHighlight') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span><input v-model="context.doHighlight" type="checkbox" class="input-sm" disabled></span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span><i class="fa fa-rss fa-lg" /></span>
      </dt>
      <dd class="col-sm-7">
        <span><a :href="`${appUrl}feed/rss/${context.context ? context.context.organization.id : ''}?pid=${context.id}`" target="_blank">{{ appUrl }}feed/rss/{{ context.context ? context.context.organization.id : '' }}?pid={{ context.id }}</a></span>
      </dd>
    </dl>

    <div
      id="savePublisherModal"
      ref="savePublisherModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myPublisherLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 id="myPublisherLabel" class="modal-title">
                {{ $t('publisher.home.createOrEditLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" />
            </div>
            <div class="modal-body">
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="editedContext.id" type="text" class="form-control" name="id" disabled>
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label class="control-label" for="context">{{ $t('publisher.context.key') }}</label>
                <input
                  id="context"
                  type="text"
                  class="form-control"
                  name="context"
                  :value="
                    editedContext.context
                      ? `${editedContext.context.reader.displayName
                      } - ${
                        editedContext.context.redactor.displayName
                      } - ${
                        editedContext.context.organization.name}`
                      : ''
                  "
                  disabled
                >
              </div>
              <div class="form-group">
                <label for="displayName" class="control-label">{{ $t('publisher.displayName') }}</label>
                <input
                  id="displayName"
                  v-model="editedContextDisplayName"
                  type="text"
                  class="form-control"
                  name="displayName"
                  required
                  minlength="3"
                  maxlength="50"
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
                <div v-if="formValidator.hasError('displayName', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('displayName', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('displayName', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '50' }) }}
                </div>
              </div>
              <div v-if="canCreateCategory" class="form-group">
                <label class="control-label" for="permissionType">{{ $t('publisher.permissionType') }}</label>
                <select
                  id="permissionType"
                  v-model="editedContextPermissionType"
                  class="form-select"
                  name="permissionType"
                  required
                  :disabled="!canManage"
                >
                  <option v-for="permission in autorizedPermissionClassList" :key="permission.id" :value="permission.name">
                    {{ $t(permission.label) }}
                  </option>
                </select>
                <div v-if="formValidator.hasError('permissionType', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group">
                <label for="used" class="control-label">{{ $t('publisher.used') }}</label>
                <input
                  id="used"
                  v-model="editedContextUsed"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="used"
                  :disabled="!canManage"
                >
              </div>
              <div v-if="publisher.context.redactor.writingMode !== 'STATIC'" class="form-group">
                <label class="control-label" for="defaultItemsDisplayOrder">{{ $t('publisher.defaultItemsDisplayOrder') }}</label>
                <select
                  id="defaultItemsDisplayOrder"
                  v-model="editedContextDefaultItemsDisplayOrder"
                  class="form-select"
                  name="defaultItemsDisplayOrder"
                  required
                  :disabled="!canManage"
                >
                  <option v-for="displayOrderType in itemsDisplayOrderTypeList" :key="displayOrderType.id" :value="displayOrderType.name">
                    {{ $t(displayOrderType.label) }}
                  </option>
                </select>
                <div v-if="formValidator.hasError('defaultItemsDisplayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group">
                <label class="control-label" for="defaultDisplayOrder">{{ $t('publisher.defaultDisplayOrder') }}</label>
                <select
                  id="defaultDisplayOrder"
                  v-model="editedContextDefaultDisplayOrder"
                  class="form-select"
                  name="defaultDisplayOrder"
                  required
                  :disabled="!canManage"
                  @change="onChangeDefaultDisplayOrder()"
                >
                  <option
                    v-for="displayOrderType in autorizedDisplayOrderTypeList"
                    :key="displayOrderType.id"
                    :value="displayOrderType.name"
                  >
                    {{ $t(displayOrderType.label) }}
                  </option>
                </select>
                <div v-if="formValidator.hasError('defaultDisplayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label for="displayOrder" class="control-label">{{ $t('publisher.displayOrder') }}</label>
                <input
                  id="displayOrder"
                  v-model="editedContextDisplayOrder"
                  type="number"
                  class="form-control"
                  name="displayOrder"
                  required
                  min="0"
                  max="9"
                  :disabled="!isInRole('ROLE_ADMIN') || editedContext.defaultDisplayOrder !== 'CUSTOM'"
                >
                <div v-if="formValidator.hasError('displayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('displayOrder', formErrors.MIN_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.min', { min: '0' }) }}
                </div>
                <div v-if="formValidator.hasError('displayOrder', formErrors.MAX_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.max', { max: '9' }) }}
                </div>
              </div>
              <div v-if="canCreateCategory" class="form-group">
                <label for="hasSubPermsManagement" class="control-label">{{ $t('publisher.hasSubPermsManagement') }}</label>
                <input
                  id="hasSubPermsManagement"
                  v-model="editedContextHasSubPermsManagement"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="hasSubPermsManagement"
                  disabled
                >
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label for="doHighlight" class="control-label">{{ $t('publisher.doHighlight') }}</label>
                <input
                  id="doHighlight"
                  v-model="editedContextDoHighlight"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="doHighlight"
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError() || !canManage" @click="confirmUpdate">
                <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div ref="deletePublisherConfirmation" class="modal fade">
      <div class="modal-dialog">
        <div class="modal-content">
          <form name="deleteForm">
            <div class="modal-header">
              <h4 class="modal-title">
                {{ $t('entity.delete.title') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" />
            </div>
            <div class="modal-body">
              <p>{{ $t('publisher.delete.question', { id: contextName }) }}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete()">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <CategoryForm ref="categoryForm" />
  </div>
</template>
