<script>
import { Modal } from 'bootstrap'
import EnumDatasService from '@/services/entities/enum/EnumDatasService.js'
import RedactorService from '@/services/entities/redactor/RedactorService.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'

export default {
  name: 'Redactor',
  data() {
    return {
      redactors: [],
      redactor: {
        name: null,
        displayName: null,
        description: null,
        format: null,
        writingMode: null,
        nbLevelsOfClassification: 1,
        optionalPublishTime: false,
        nbDaysMaxDuration: 168,
        id: null,
      },
      deleteModal: null,
      updateModal: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
    }
  },
  computed: {
    writingFormatList() {
      return EnumDatasService.getWritingFormatList()
    },
    writingModeList() {
      return EnumDatasService.getWritingModeList()
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'redactor.name': function (newVal) {
      this.formValidator.checkTextFieldValidity('name', newVal, 3, 20, true)
    },
    'redactor.displayName': function (newVal) {
      this.formValidator.checkTextFieldValidity('displayName', newVal, 3, 50, true)
    },
    'redactor.description': function (newVal) {
      this.formValidator.checkTextFieldValidity('description', newVal, 5, 512, true)
    },
    'redactor.format': function (newVal) {
      this.formValidator.checkTextFieldValidity('format', newVal, null, null, true)
    },
    'redactor.writingMode': function (newVal) {
      this.formValidator.checkTextFieldValidity('writingMode', newVal, null, null, true)
    },
    'redactor.nbLevelsOfClassification': function (newVal) {
      this.formValidator.checkNumberFieldValidity('nbLevelsOfClassification', newVal, 1, 2, true)
    },
    'redactor.nbDaysMaxDuration': function (newVal) {
      this.formValidator.checkNumberFieldValidity('nbDaysMaxDuration', newVal, 1, 999, true)
    },
  },
  mounted() {
    this.deleteModal = new Modal(this.$refs.deleteRedactorConfirmation)
    this.updateModal = new Modal(this.$refs.saveRedactorModal)
    this.loadAll()
  },
  methods: {
    // Méthode permettant de récupérer la liste des objets redacteurs
    loadAll() {
      RedactorService.query()
        .then((response) => {
          this.redactors = response.data
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('name', this.redactor.name, 3, 20, true)
      this.formValidator.checkTextFieldValidity('displayName', this.redactor.displayName, 3, 50, true)
      this.formValidator.checkTextFieldValidity('description', this.redactor.description, 5, 512, true)
      this.formValidator.checkTextFieldValidity('format', this.redactor.format, null, null, true)
      this.formValidator.checkTextFieldValidity('writingMode', this.redactor.writingMode, null, null, true)
      this.formValidator.checkNumberFieldValidity('nbLevelsOfClassification', this.redactor.nbLevelsOfClassification, 1, 2, true)
      this.formValidator.checkNumberFieldValidity('nbDaysMaxDuration', this.redactor.nbDaysMaxDuration, 1, 999, true)
    },
    reset() {
      this.redactors = []
      this.loadAll()
    },
    // Méthode de création et de mise à jour de redacteur
    createRedactor() {
      RedactorService.update(this.redactor)
        .then(() => {
          this.updateModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    // Méthode en charge d'ouvrir la modale de mise à jour de redacteur
    update(id) {
      RedactorService.get(id)
        .then((response) => {
          this.redactor = response.data
          this.initFormValidator()
          this.updateModal.show()
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    // Méthode en charge d'ouvrir la modale de suppression de redacteur
    deleteRedactor(id) {
      RedactorService.get(id)
        .then((response) => {
          this.redactor = response.data
          this.deleteModal.show()
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    // Méthode en charge de supprimer une redacteur
    confirmDelete(id) {
      RedactorService.delete(id)
        .then(() => {
          this.deleteModal.hide()
          this.reset()
          this.clear()
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        })
    },
    clear() {
      this.redactor = {
        name: null,
        displayName: null,
        description: null,
        format: this.writingFormatList[0],
        writingMode: this.writingModeList[0].name,
        nbLevelsOfClassification: 1,
        optionalPublishTime: false,
        nbDaysMaxDuration: 168,
        id: null,
      }
    },
    redactorDetail(redactorId) {
      this.$router.push({
        name: 'AdminEntityRedactorDetails',
        params: { id: redactorId },
      })
    },
    getWritingModeLabel(name) {
      if (name) {
        return this.writingModeList.filter((val) => {
          return val.name === name
        })[0].label
      }
      else {
        return ''
      }
    },
  },
}
</script>

<template>
  <div class="redactor">
    <h2>{{ $t('redactor.home.title') }}</h2>
    <button
      class="btn btn-primary btn-lg"
      data-bs-toggle="modal"
      data-bs-target="#saveRedactorModal"
      @click="
        clear();
        initFormValidator();
      "
    >
      <span class="fas fa-bolt" />
      <span>{{ $t('redactor.home.createLabel') }}</span>
    </button>
    <div
      id="saveRedactorModal"
      ref="saveRedactorModal"
      class="modal fade"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myRedactorLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-fullscreen-md-down modal-lg">
        <div class="modal-content">
          <form name="editForm" role="form" class="was-validated" novalidate show-validation>
            <div class="modal-header">
              <h4 id="myRedactorLabel" class="modal-title">
                {{ $t('redactor.home.createOrEditLabel') }}
              </h4>
              <button type="button" class="btn-close" aria-hidden="true" data-bs-dismiss="modal" @click="clear" />
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="control-label" for="ID">ID</label>
                <input id="ID" v-model="redactor.id" type="text" class="form-control" name="id" disabled>
              </div>

              <div class="form-group">
                <label for="name" class="control-label">{{ $t('redactor.name') }}</label>
                <input
                  id="name"
                  v-model="redactor.name"
                  type="text"
                  class="form-control"
                  name="name"
                  required
                  minlength="3"
                  maxlength="20"
                >
                <div v-if="formValidator.hasError('name', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MIN_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.minlength', { min: '3' }) }}
                </div>
                <div v-if="formValidator.hasError('name', formErrors.MAX_LENGTH)" class="invalid-feedback">
                  {{ $t('entity.validation.maxlength', { max: '20' }) }}
                </div>
              </div>
              <div class="form-group">
                <label for="displayName" class="control-label">{{ $t('redactor.displayName') }}</label>
                <input
                  id="displayName"
                  v-model="redactor.displayName"
                  type="text"
                  class="form-control"
                  name="displayName"
                  required
                  minlength="3"
                  maxlength="50"
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
              <div class="form-group">
                <label for="description" class="control-label">{{ $t('redactor.description') }}</label>
                <input
                  id="description"
                  v-model="redactor.description"
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
                <label for="format" class="control-label">{{ $t('redactor.format') }}</label>
                <select id="format" v-model="redactor.format" class="form-select" required>
                  <option v-for="format in writingFormatList" :key="format.id" :value="format">
                    {{ format }}
                  </option>
                </select>
                <div v-if="formValidator.hasError('format', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group">
                <label for="writingMode" class="control-label">{{ $t('redactor.writingMode') }}</label>
                <select id="writingMode" v-model="redactor.writingMode" class="form-select" required>
                  <option v-for="writingMode in writingModeList" :key="writingMode.id" :value="writingMode.name">
                    {{ $t(getWritingModeLabel(writingMode.name)) }}
                  </option>
                </select>
                <div v-if="formValidator.hasError('writingMode', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
              </div>
              <div class="form-group">
                <label for="nbLevelsOfClassification" class="control-label">{{ $t('redactor.nbLevelsOfClassification') }}</label>
                <input
                  id="nbLevelsOfClassification"
                  v-model="redactor.nbLevelsOfClassification"
                  type="number"
                  class="form-control"
                  name="nbLevelsOfClassification"
                  required
                  min="1"
                  max="2"
                >
                <div v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.MIN_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.min', { min: '1' }) }}
                </div>
                <div v-if="formValidator.hasError('nbLevelsOfClassification', formErrors.MAX_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.max', { max: '2' }) }}
                </div>
              </div>
              <div class="form-group">
                <label for="optionalPublishTime" class="control-label">{{ $t('redactor.optionalPublishTime') }}</label>
                <input
                  id="optionalPublishTime"
                  v-model="redactor.optionalPublishTime"
                  type="checkbox"
                  class="form-check-input d-block mx-auto"
                  name="optionalPublishTime"
                  value="false"
                >
              </div>
              <div class="form-group">
                <label for="nbDaysMaxDuration" class="control-label">{{ $t('redactor.nbDaysMaxDuration') }}</label>
                <input
                  id="nbDaysMaxDuration"
                  v-model="redactor.nbDaysMaxDuration"
                  type="number"
                  class="form-control"
                  name="nbDaysMaxDuration"
                  required
                  min="1"
                  max="999"
                >
                <div v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.REQUIRED)" class="invalid-feedback">
                  {{ $t('entity.validation.required') }}
                </div>
                <div v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.MIN_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.min', { min: '1' }) }}
                </div>
                <div v-if="formValidator.hasError('nbDaysMaxDuration', formErrors.MAX_VALUE)" class="invalid-feedback">
                  {{ $t('entity.validation.max', { max: '999' }) }}
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                  <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
                </button>
                <button type="button" class="btn btn-primary" :disabled="formValidator.hasError()" @click="createRedactor">
                  <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div ref="deleteRedactorConfirmation" class="modal fade">
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
              <p>{{ $t('redactor.delete.question', { id: redactor.id }) }}</p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default btn-outline-dark" data-bs-dismiss="modal" @click="clear">
                <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
              </button>
              <button type="button" class="btn btn-danger" @click="confirmDelete(redactor.id)">
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
            <th>{{ $t('redactor.name') }}</th>
            <th>{{ $t('redactor.displayName') }}</th>
            <th>{{ $t('redactor.description') }}</th>
            <th>{{ $t('redactor.format') }}</th>
            <th>{{ $t('redactor.writingMode') }}</th>
            <th>{{ $t('redactor.nbLevelsOfClassification') }}</th>
            <th>{{ $t('redactor.optionalPublishTime') }}</th>
            <th>{{ $t('redactor.nbDaysMaxDuration') }}</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr v-for="redactor in redactors" :key="redactor.id">
            <td>
              <router-link
                :to="{
                  name: 'AdminEntityRedactorDetails',
                  params: { id: redactor.id },
                }"
              >
                {{ redactor.id }}
              </router-link>
            </td>
            <td>{{ redactor.name }}</td>
            <td>{{ redactor.displayName }}</td>
            <td>{{ redactor.description }}</td>
            <td>{{ redactor.format }}</td>
            <td>{{ $t(getWritingModeLabel(redactor.writingMode)) }}</td>
            <td>{{ redactor.nbLevelsOfClassification }}</td>
            <td>
              <input v-model="redactor.optionalPublishTime" type="checkbox" disabled>
            </td>
            <td>{{ redactor.nbDaysMaxDuration }}</td>
            <td>
              <button type="submit" class="btn btn-info btn-sm me-1" @click="redactorDetail(redactor.id)">
                <span class="far fa-eye" />&nbsp;<span>{{ $t('entity.action.view') }}</span>
              </button>
              <button type="submit" class="btn btn-primary btn-sm me-1" @click="update(redactor.id)">
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button type="submit" class="btn btn-danger btn-sm" @click="deleteRedactor(redactor.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
