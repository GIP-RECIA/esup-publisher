<script>
import { Modal } from 'bootstrap'
import OrganizationService from '@/services/entities/organization/OrganizationService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'

export default {
  name: 'Organization',
  data() {
    return {
      organizations: [],
      organization: {
        name: null,
        displayName: null,
        description: null,
        displayOrder: 0,
        allowNotifications: false,
        identifiers: [],
        id: null,
      },
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'organization.name': function (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 5, 255, true)
    },
    'organization.description': function (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true)
    },
    'organization.identifiers': function (newVal) {
      this.formValidator.checkTextFieldValidity('identifiers', newVal, null, null, true)
    },
    'organization.displayOrder': function (newVal) {
      this.formValidator.checkNumberFieldValidity('displayOrder', newVal, 0, 999, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteOrganizationConfirmation)
    this.updateModal = new Modal(this.$refs.saveOrganizationModal)
    this.loadAll()
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets structures
    loadAll() {
      OrganizationService.query()
        .then((response) => {
          this.organizations = response.data
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.organization.name, 5, 255, true)
      this.formValidator.checkTextFieldValidity('description', this.organization.description, 5, 512, true)
      this.formValidator.checkTextFieldValidity('identifiers', this.organization.identifiers, null, null, true)
      this.formValidator.checkNumberFieldValidity('displayOrder', this.organization.displayOrder, 0, 999, true)
    },
    reset() {
      this.organizations = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de structure
    createOrganization() {
      this.organization.displayName = this.organization.name
      if (typeof this.organization.identifiers === 'string') {
        let identifiersValues = this.organization.identifiers
        this.organization.identifiers = []
        if (identifiersValues.includes(',')) {
          identifiersValues = identifiersValues.split(',')
          identifiersValues.forEach((identifiersValue) => {
            this.organization.identifiers.push(identifiersValue)
          })
        }
        else {
          this.organization.identifiers.push(identifiersValues)
        }
      }
      OrganizationService.update(this.organization)
        .then(() => {
          this.updateModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de structure
    update(id) {
      OrganizationService.get(id)
        .then((response) => {
          this.organization = response.data
          this.initFormValidator()
          this.updateModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge d'ouvrir la modale de suppression de structure
    deleteOrganization(id) {
      OrganizationService.get(id)
        .then((response) => {
          this.organization = response.data
          this.deleteModal.show()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    // Méthode en charge de supprimer une structure
    confirmDelete(id) {
      OrganizationService.delete(id)
        .then(() => {
          this.deleteModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          console.error(error)
        })
    },
    clear() {
      this.organization = {
        name: null,
        displayName: null,
        description: null,
        displayOrder: 0,
        allowNotifications: false,
        identifiers: [],
        id: null,
      }
    },
    organizationDetail(organizationId) {
      this.$router.push({
        name: 'AdminEntityOrganizationDetails',
        params: { id: organizationId },
      })
    },
  },
}
</script>

<template>
  <div class="organization">
    <h2>{{ $t('organization.home.title') }}</h2>
    <button
      v-has-any-role="'ROLE_ADMIN'"
      class="btn btn-primary btn-lg"
      data-bs-toggle="modal"
      data-bs-target="#saveOrganizationModal"
      @click="
        clear();
        initFormValidator();
      "
    >
      <span class="fas fa-bolt" />
      <span>{{ $t('organization.home.createLabel') }}</span>
    </button>
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
                {{ $t('organization.home.createOrEditLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clear" />
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="organization.id" type="text" class="form-control" name="id" disabled>
              </div>

              <div class="form-group">
                <label for="name" class="control-label">{{ $t('organization.name') }}</label>
                <input
                  id="name"
                  v-model="organization.name"
                  type="text"
                  class="form-control"
                  name="name"
                  required
                  minlength="5"
                  maxlength="255"
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
                  v-model="organization.description"
                  type="text"
                  class="form-control"
                  name="description"
                  required
                  minlength="5"
                  maxlength="512"
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
              <div class="form-group">
                <label for="identifiers" class="control-label">{{ $t('organization.identifiers') }}</label>
                <input
                  id="identifiers"
                  v-model="organization.identifiers"
                  type="text"
                  name="identifiers"
                  class="form-control"
                  :placeholder="$t('organization.identifiers-help')"
                  required
                >
                <div v-if="formValidator.hasError('identifiers', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group">
                <label for="displayOrder" class="control-label">{{ $t('organization.displayOrder') }}</label>
                <input
                  id="displayOrder"
                  v-model="organization.displayOrder"
                  type="number"
                  class="form-control"
                  name="displayOrder"
                  required
                  min="0"
                  max="999"
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
              <div class="form-group">
                <label for="allowNotifications" class="control-label">{{ $t('organization.allowNotifications') }}</label>
                <input
                  id="allowNotifications"
                  v-model="organization.allowNotifications"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="allowNotifications"
                  value="true"
                >
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-primary" :disabled="formValidator.hasError()" @click="createOrganization">
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
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clear" />
            </div>
            <div class="modal-body">
              <p>
                {{ $t('organization.delete.question', { id: organization.id }) }}
              </p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete(organization.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div class="table-responsive">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>{{ $t('organization.name') }}</th>
            <th>{{ $t('organization.description') }}</th>
            <th>{{ $t('organization.displayOrder') }}</th>
            <th>{{ $t('organization.allowNotifications') }}</th>
            <th>{{ $t('organization.identifiers') }}</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr v-for="organization in organizations" :key="organization.id">
            <td>
              <router-link
                :to="{
                  name: 'AdminEntityOrganizationDetails',
                  params: { id: organization.id },
                }"
              >
                {{ organization.id }}
              </router-link>
            </td>
            <td>{{ organization.name }}</td>
            <td>{{ organization.description }}</td>
            <td>{{ organization.displayOrder }}</td>
            <td>
              <input v-model="organization.allowNotifications" type="checkbox" disabled>
            </td>
            <td>
              <span v-for="identifierId in organization.identifiers" :key="identifierId" class="list-comma">{{ identifierId }}</span>
            </td>
            <td>
              <button type="submit" class="btn btn-info btn-sm me-1" @click="organizationDetail(organization.id)">
                <span class="far fa-eye" />&nbsp;<span>{{ $t('entity.action.view') }}</span>
              </button>
              <button v-has-any-role="'ROLE_ADMIN'" type="submit" class="btn btn-primary btn-sm me-1" @click="update(organization.id)">
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button
                v-has-any-role="'ROLE_ADMIN'"
                type="submit"
                class="btn btn-danger btn-sm"
                @click="deleteOrganization(organization.id)"
              >
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
