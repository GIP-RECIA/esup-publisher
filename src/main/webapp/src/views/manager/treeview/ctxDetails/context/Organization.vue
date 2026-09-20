<script>
import { Modal } from 'bootstrap'
import PrincipalService from '@/services/auth/PrincipalService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'

export default {
  name: 'Organization',
  inject: [
    'context',
    'editedContext',
    'setEditedContext',
    'updateContext',
    'confirmDeleteContext',
    'confirmUpdateContext',
    'canManage',
    'appUrl',
  ],
  data() {
    return {
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  computed: {
    editedContextName: {
      get() {
        return this.editedContext.name || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.name = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextDescription: {
      get() {
        return this.editedContext.description || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.description = newVal
        this.setEditedContext(newEditedContext)
      },
    },
    editedContextIdentifiers: {
      get() {
        return this.editedContext.identifiers || ''
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.identifiers = newVal
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
    editedContextAllowNotifications: {
      get() {
        return this.editedContext.allowNotifications || false
      },
      set(newVal) {
        const newEditedContext = Object.assign({}, this.editedContext)
        newEditedContext.allowNotifications = newVal
        this.setEditedContext(newEditedContext)
      },
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'editedContext.name': function (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 5, 255, true)
    },
    'editedContext.description': function (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true)
    },
    'editedContext.identifiers': function (newVal) {
      this.formValidator.checkTextFieldValidity('identifiers', newVal, null, null, true)
    },
    'editedContext.displayOrder': function (newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 999, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteOrganizationConfirmation)
    this.updateModal = new Modal(this.$refs.saveOrganizationModal)
  },
  methods: {
    isInRole(role) {
      return PrincipalService.isInRole(role)
    },
    // Méthode de création et de mise à jour de structure
    confirmUpdate() {
      this.confirmUpdateContext('ORGANIZATION', () => {
        this.updateModal.hide()
      })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de structure
    updateOrganization() {
      this.updateContext(() => {
        this.initFormValidator()
        this.updateModal.show()
      })
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.editedContext.name, 5, 255, true)
      this.formValidator.checkTextFieldValidity('description', this.editedContext.description, 5, 512, true)
      this.formValidator.checkTextFieldValidity('identifiers', this.editedContext.identifiers, null, null, true)
      this.formValidator.checkNumberFieldValidity('displayOrder', this.editedContext.displayOrder, 0, 999, true)
    },
    // Méthode en charge d'ouvrir la modale de suppression de structure
    deleteOrganization() {
      this.deleteModal.show()
    },
    // Méthode en charge de supprimer une structure
    confirmDelete() {
      this.confirmDeleteContext(() => {
        this.deleteModal.hide()
      })
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
      <button v-has-role="'ROLE_ADMIN'" type="button" class="btn btn-primary btn-sm me-1" @click="updateOrganization()">
        <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
      </button>
      <button v-has-role="'ROLE_ADMIN'" type="button" class="btn btn-danger btn-sm me-1" @click="deleteOrganization()">
        <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
      </button>
    </div>

    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.name') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.name }}</span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.description') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.description }}</span>
      </dd>
    </dl>
    <dl v-has-role="'ROLE_ADMIN'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.displayOrder') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span>{{ context.displayOrder }}</span>
      </dd>
    </dl>
    <dl v-has-role="'ROLE_ADMIN'" class="row entity-details">
      <dt class="col-sm-5">
        <span>{{ $t('organization.allowNotifications') }}</span>
      </dt>
      <dd class="col-sm-7">
        <span><input v-model="context.allowNotifications" type="checkbox" disabled></span>
      </dd>
    </dl>
    <dl class="row entity-details">
      <dt class="col-sm-5">
        <i class="fa fa-rss fa-lg" />
      </dt>
      <dd class="col-sm-7">
        <a :href="`${appUrl}feed/rss/${context.id}`" target="_blank">{{ appUrl }}feed/rss/{{ context.id }}</a>
      </dd>
    </dl>

    <div
      id="saveOrganizationModal"
      ref="saveOrganizationModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myOrganizationLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 id="myOrganizationLabel" class="modal-title">
                {{ $t('organization.home.editLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" />
            </div>
            <div class="modal-body">
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="editedContext.id" type="text" class="form-control" name="id" disabled>
              </div>
              <div class="form-group">
                <label for="name" class="control-label">{{ $t('organization.name') }}</label>
                <input
                  id="name"
                  v-model="editedContextName"
                  type="text"
                  class="form-control"
                  name="name"
                  required
                  minlength="5"
                  maxlength="255"
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
                <div v-if="formValidator.hasError('name', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '5' }) }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '255' }) }}
                </div>
              </div>
              <div class="form-group">
                <label for="description" class="control-label">{{ $t('organization.description') }}</label>
                <input
                  id="description"
                  v-model="editedContextDescription"
                  type="text"
                  class="form-control"
                  name="description"
                  required
                  minlength="5"
                  maxlength="512"
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
                <div v-if="formValidator.hasError('description', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('description', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '5' }) }}
                </div>
                <div v-if="formValidator.hasError('description', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '512' }) }}
                </div>
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label for="identifiers" class="control-label">{{ $t('organization.identifiers') }}</label>
                <input
                  id="identifiers"
                  v-model="editedContextIdentifiers"
                  type="text"
                  name="identifiers"
                  class="form-control"
                  :placeholder="$t('organization.identifiers-help')"
                  required
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
                <div v-if="formValidator.hasError('identifiers', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label for="displayOrder" class="control-label">{{ $t('organization.displayOrder') }}</label>
                <input
                  id="displayOrder"
                  v-model="editedContextDisplayOrder"
                  type="number"
                  class="form-control"
                  name="displayOrder"
                  required
                  min="0"
                  max="999"
                  :disabled="!isInRole('ROLE_ADMIN')"
                >
                <div v-if="formValidator.hasError('displayOrder', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('displayOrder', formErrors.MIN_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.min', { min: '0' }) }}
                </div>
                <div v-if="formValidator.hasError('displayOrder', formErrors.MAX_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.max', { max: '999' }) }}
                </div>
              </div>
              <div v-has-role="'ROLE_ADMIN'" class="form-group">
                <label for="allowNotifications" class="control-label">{{ $t('organization.allowNotifications') }}</label>
                <input
                  id="allowNotifications"
                  v-model="editedContextAllowNotifications"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="allowNotifications"
                  :disabled="!canManage"
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

    <div ref="deleteOrganizationConfirmation" class="modal fade">
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
              <p>
                {{ $t('organization.delete.question', { id: context.name }) }}
              </p>
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
  </div>
</template>
