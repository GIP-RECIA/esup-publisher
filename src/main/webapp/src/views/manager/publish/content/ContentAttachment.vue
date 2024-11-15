<script>
import ConfigurationService from '@/services/params/ConfigurationService.js'
import DateUtils from '@/services/util/DateUtils.js'
import { FormErrorType, FormValidationUtils } from '@/services/util/FormValidationUtils.js'
import UploadUtils from '@/services/util/UploadUtils.js'
import store from '@/store/index.js'

export default {
  name: 'ContentAttachment',
  inject: [
    'publisher',
    'item',
    'setItem',
    'setItemValidated',
    'minDate',
    'endMinDate',
    'maxDate',
    'linkedFilesToContent',
    'setLinkedFilesToContent',
    'progress',
    'setProgress',
    'progressStatus',
    'uploadLinkedFile',
    'deleteAttachment',
    'authorizedMimeTypes',
    'startMaxDate',
  ],
  data() {
    return {
      fileInput: null,
      fileInputError: null,
      formValidator: new FormValidationUtils(),
      formErrors: FormErrorType,
      dragActive: false,
    }
  },
  computed: {
    itemTitle: {
      get() {
        return this.item.title || ''
      },
      set(newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.title = newVal
        this.setItem(newItem)
      },
    },
    itemSummary: {
      get() {
        return this.item.summary
      },
      set(newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.summary = newVal
        this.setItem(newItem)
      },
    },
    itemStartDate: {
      get() {
        return this.formatDateToString(this.item.startDate)
      },
      set(newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.startDate = this.formatStringToDate(newVal)
        this.setItem(newItem)
      },
    },
    itemEndDate: {
      get() {
        return this.formatDateToString(this.item.endDate)
      },
      set(newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.endDate = this.formatStringToDate(newVal)
        this.setItem(newItem)
      },
    },
    itemRssAllowed: {
      get() {
        return this.item.rssAllowed
      },
      set(newVal) {
        const newItem = Object.assign({}, this.item)
        newItem.rssAllowed = newVal
        this.setItem(newItem)
      },
    },
    filteredLinkedFilesToContent() {
      return (this.linkedFilesToContent || []).filter(file => !file.inBody)
    },
  },
  // Listeners en charge de vérifier la validité des champs du formulaire
  watch: {
    'item.title': function (newVal) {
      this.formValidator.checkTextFieldValidity('title', newVal, 3, 200, true)
      this.updateItemValidated()
    },
    'item.summary': function (newVal) {
      this.formValidator.checkTextFieldValidity('summary', newVal, 5, 512, true)
      this.updateItemValidated()
    },
    'item.startDate': function (newVal) {
      this.formValidator.checkDateFieldValidity('startDate', newVal, this.minDate, this.startMaxDate, true)
      this.formValidator.checkDateFieldValidity(
        'endDate',
        this.item.endDate,
        this.endMinDate,
        this.maxDate,
        !this.publisher.context.redactor.optionalPublishTime,
      )
      this.updateItemValidated()
    },
    'item.endDate': function (newVal) {
      this.formValidator.checkDateFieldValidity(
        'endDate',
        newVal,
        this.endMinDate,
        this.maxDate,
        !this.publisher.context.redactor.optionalPublishTime,
      )
      this.updateItemValidated()
    },
    'linkedFilesToContent': function (newVal) {
      this.formValidator.checkArrayFieldValidity('files', newVal, null, null, true)
      this.updateItemValidated()
    },
  },
  mounted() {
    this.fileInput = this.$refs.fileInput
  },
  created() {
    this.initFormValidator()
  },
  methods: {
    // Méthode permettant d'initialiser le FormValidator
    initFormValidator() {
      this.formValidator.clear()
      this.formValidator.checkTextFieldValidity('title', this.item.title, 3, 200, true)
      this.formValidator.checkTextFieldValidity('summary', this.item.summary, 5, 512, true)
      this.formValidator.checkDateFieldValidity('startDate', this.item.startDate, this.minDate, this.startMaxDate, true)
      this.formValidator.checkDateFieldValidity(
        'endDate',
        this.item.endDate,
        this.endMinDate,
        this.maxDate,
        !this.publisher.context.redactor.optionalPublishTime,
      )
      this.formValidator.checkArrayFieldValidity('files', this.linkedFilesToContent, null, null, true)
      this.updateItemValidated()
    },
    updateItemValidated() {
      this.setItemValidated(!this.formValidator.hasError())
    },
    formatDateToString(date) {
      return DateUtils.convertLocalDateToServer(date)
    },
    formatDateToIntString(date) {
      return DateUtils.formatDateToShortIntString(date, store.getters.getLanguage)
    },
    formatStringToDate(date) {
      return DateUtils.convertLocalDateFromServer(date)
    },
    getCssFileFromType(fileType, fileName) {
      return UploadUtils.getCssFileFromType(fileType, fileName)
    },
    onSelectFile() {
      const file = this.fileInput.files ? this.fileInput.files[0] : null
      this.uploadAttachment(file)
    },
    onDropFile(event) {
      this.dragActive = false
      const file = event.dataTransfer.files ? event.dataTransfer.files[0] : null
      this.uploadAttachment(file)
    },
    uploadAttachment(file) {
      this.fileInputError = null
      if (file) {
        // Vérification de la validatié du fichier sélectionnée
        this.fileInputError = FormValidationUtils.getFileFieldError(
          file,
          `(${this.authorizedMimeTypes.join('|')})`,
          ConfigurationService.getConfUploadFileSize(),
          false,
        )

        if (this.fileInputError === null) {
          const isImage = file.type.match('image/*') !== null
          if (isImage) {
            UploadUtils.convertImageToJpeg(file)
              .then((jpeg) => {
                this.doUpload(jpeg)
              })
              .catch((err) => {
                console.error(err)
                this.doUpload(file)
              })
          }
          else {
            this.doUpload(file)
          }
        }
        else {
          this.fileInput.value = null
        }
      }
    },
    doUpload(file) {
      this.uploadLinkedFile(file, file.name, false, false, (response, headers) => {
        const location = decodeURIComponent(headers.location)
        const newLinks = Array.from(this.linkedFilesToContent || [])
        newLinks.push({
          uri: location,
          filename: file.name,
          inBody: false,
          contentType: file.type,
        })
        this.setLinkedFilesToContent(newLinks)
        this.fileInput.value = null
        setTimeout(() => {
          this.setProgress(null)
        }, 3000)
      })
    },
    getUrlAttachment(attachment) {
      if (attachment.uri) {
        return UploadUtils.getInternalUrl(attachment.uri)
      }
    },
  },
}
</script>

<template>
  <div>
    <div class="attachmentForm">
      <div class="form-group d-none">
        <label class="control-label" for="ID">ID</label>
        <input id="ID" v-model="item.id" type="text" class="form-control" name="id" readonly>
      </div>

      <div class="form-group">
        <label class="control-label" for="title">{{ $t('attachment.title') }}</label>
        <input id="title" v-model="itemTitle" type="text" class="form-control" name="title" required minlength="3" maxlength="200">
        <div v-if="formValidator.hasError('title', formErrors.REQUIRED)" class="invalid-feedback">
          {{ $t('entity.validation.required') }}
        </div>
        <div v-if="formValidator.hasError('title', formErrors.MIN_LENGTH)" class="invalid-feedback">
          {{ $t('entity.validation.minlength', { min: '3' }) }}
        </div>
        <div v-if="formValidator.hasError('title', formErrors.MAX_LENGTH)" class="invalid-feedback">
          {{ $t('entity.validation.maxlength', { max: '200' }) }}
        </div>
      </div>
      <div class="form-group">
        <label class="control-label" for="summary">{{ $t('attachment.summary') }}</label>
        <textarea id="summary" v-model="itemSummary" class="form-control" name="summary" rows="3" required minlength="5" maxlength="512" />
        <div v-if="formValidator.hasError('summary', formErrors.REQUIRED)" class="invalid-feedback">
          {{ $t('entity.validation.required') }}
        </div>
        <div v-if="formValidator.hasError('summary', formErrors.MIN_LENGTH)" class="invalid-feedback">
          {{ $t('entity.validation.minlength', { min: '5' }) }}
        </div>
        <div v-if="formValidator.hasError('summary', formErrors.MAX_LENGTH)" class="invalid-feedback">
          {{ $t('entity.validation.maxlength', { min: '512' }) }}
        </div>
      </div>
      <div class="row">
        <div class="form-group col-md-4">
          <label class="control-label" for="startDate">{{ $t('attachment.startDate') }}</label>
          <input
            id="startDate"
            v-model="itemStartDate"
            type="date"
            class="form-control"
            name="startDate"
            placeholder="jj/mm/aaaa"
            required
            :min="formatDateToString(minDate)"
            :max="formatDateToString(startMaxDate)"
          >
          <div v-if="formValidator.hasError('startDate', formErrors.REQUIRED)" class="invalid-feedback">
            {{ $t('entity.validation.required') }}
          </div>
          <div v-if="formValidator.hasError('startDate', formErrors.MIN_DATE)" class="invalid-feedback">
            {{
              $t('entity.validation.mindate', {
                min: formatDateToIntString(minDate),
              })
            }}
          </div>
          <div v-if="formValidator.hasError('startDate', formErrors.MAX_DATE)" class="invalid-feedback">
            {{
              $t('entity.validation.maxdate', {
                max: formatDateToIntString(startMaxDate),
              })
            }}
          </div>
        </div>
        <div class="form-group col-md-4">
          <label class="control-label" for="endDate">{{ $t('attachment.endDate') }}</label>
          <span v-if="publisher.context.redactor.optionalPublishTime" class="d-block d-md-none">{{
            $t('attachment.detail.unlimitedEndDate')
          }}</span>
          <input
            id="endDate"
            v-model="itemEndDate"
            v-tooltip="publisher.context.redactor.optionalPublishTime ? $t('attachment.detail.unlimitedEndDate') : ''"
            type="date"
            class="form-control"
            name="endDate"
            placeholder="jj/mm/aaaa"
            :min="formatDateToString(endMinDate)"
            :max="formatDateToString(maxDate)"
            :required="!publisher.context.redactor.optionalPublishTime"
          >
          <div v-if="formValidator.hasError('endDate', formErrors.REQUIRED)" class="invalid-feedback">
            {{ $t('entity.validation.required') }}
          </div>
          <div v-if="formValidator.hasError('endDate', formErrors.MIN_DATE)" class="invalid-feedback">
            {{
              $t('entity.validation.mindate', {
                min: formatDateToIntString(endMinDate),
              })
            }}
          </div>
          <div v-if="formValidator.hasError('endDate', formErrors.MAX_DATE)" class="invalid-feedback">
            {{
              $t('entity.validation.maxdate', {
                max: formatDateToIntString(maxDate),
              })
            }}
          </div>
        </div>
        <div class="form-group col-md-4 text-md-center">
          <label class="control-label" for="rssAllowed">{{ $t('attachment.rssAllowed') }}</label>
          <input id="rssAllowed" v-model="itemRssAllowed" type="checkbox" class="form-check-input d-block mx-auto" name="rssAllowed">
        </div>
      </div>
      <div class="form-group">
        <label class="control-label" for="linkedFiles">{{ $t('attachment.linkedFiles') }}</label>
        <div class="form-inline">
          <div v-for="attachment in filteredLinkedFilesToContent" :key="attachment.uri" class="linkedFiles">
            <span>
              <a :href="getUrlAttachment(attachment)" target="_blank">
                <i :class="getCssFileFromType(attachment.contentType, attachment.filename)" />{{ attachment.filename }}
              </a>
              <a v-tooltip="$t('entity.action.delete')" class="mx-1" href="" @click.prevent="deleteAttachment(attachment)">
                <i class="far fa-trash-can text-danger" />
              </a>
            </span>
          </div>
        </div>
        <div v-if="formValidator.hasError('files', formErrors.REQUIRED)" class="invalid-feedback d-block">
          {{ $t('entity.validation.required') }}
        </div>
        <div v-if="fileInputError === formErrors.PATTERN" class="invalid-feedback d-block">
          {{ $t('entity.validation.filepattern') }}
        </div>
        <div v-if="fileInputError === formErrors.MAX_SIZE" class="invalid-feedback d-block">
          {{ $t('entity.validation.maxsize', { maxsize: '10MB' }) }}
        </div>
        <div v-if="progress > 0" class="progress">
          <div
            :class="`progress-bar progress-bar-striped bg-${progressStatus}`"
            role="progressbar"
            :aria-valuenow="progress"
            aria-valuemin="0"
            aria-valuemax="100"
            :style="{ width: `${progress}%` }"
          >
            {{ progress }}%
          </div>
        </div>
        <button type="button" class="btn btn-primary btn-file" name="fileEnclosure" @click="fileInput.click()">
          <i class="fas fa-folder-open" />&nbsp;<span>{{ $t('media.inputfile.upload-button') }}</span>
        </button>
        <input id="file" ref="fileInput" type="file" name="file" :accept="authorizedMimeTypes.join(',')" hidden @change="onSelectFile()">
        <div
          class="drop-area"
          :class="{ 'drag-active': dragActive }"
          @drop.prevent="onDropFile"
          @dragover.prevent="dragActive = true"
          @dragleave.prevent="dragActive = false"
        >
          <span>{{ $t('media.inputfile.drop-here') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
