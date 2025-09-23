<script>
import GroupService from '@/services/entities/group/GroupService.js'
import SubjectService from '@/services/params/SubjectService.js'
import UserService from '@/services/user/UserService.js'
import SubjectDetail from '@/views/entities/subject/SubjectDetail.vue'

export default {
  name: 'PermissionOnCtx',
  components: {
    SubjectDetail,
  },
  inject: [
    'addPerm',
    'selectedSubjects',
    'permissions',
    'permission',
    'permissionAdvanced',
    'availableRoles',
    'isDatasLoad',
    'editEvaluatorError',
    'setIsDatasLoad',
    'context',
    'userDisplayedAttrs',
    'userFonctionalAttrs',
    'deletePermission',
    'changePermissionAdvanced',
    'updateSelectedSubject',
    'removeFromSelectedSubjects',
    'areSubjectsSelected',
    'clearPermission',
    'createPermission',
    'addPermission',
    'getPermissionTypeLabel',
    'updatePermission',
    'onModificationEditEvaluator',
    'isAdvancedEvaluator',
    'subjectInfosConfig',
    'selectPermissionManager',
    'operatorTypeList',
    'stringEvaluationModeList',
  ],
  data() {
    return {
      canEditCtxPerms: false,
      subjectDetail: null,
      treeData: [],
      userFunctionnalAttributes: [],
      editEvaluatorConfig: {
        getSubjectInfos: null,
        userDisplayedAttrs: [],
        lang: this.$store.getters.getLanguage,
        operators: [],
        stringEvaluators: [],
        userAttributes: [],
        treeGroupDatas: [],
        searchUsers: null,
        getGroupMembers: null,
      },
      evaluatorConfig: {
        getSubjectInfos: null,
        userDisplayedAttrs: [],
        lang: this.$store.getters.getLanguage,
      },
      subjectSearchButtonConfig: {
        treeGroupDatas: [],
        userDisplayedAttrs: [],
        extendedAttrs: [],
        searchUsers: null,
        getGroupMembers: null,
        lang: this.$store.getters.getLanguage,
      },
    }
  },
  mounted() {
    this.subjectDetail = this.$refs.subjectDetail
  },
  created() {
    UserService.canEditCtxPerms(this.context.contextKey.keyId, this.context.contextKey.keyType).then((response) => {
      this.canEditCtxPerms = response.data.value
    })
    UserService.funtionalAttributes().then((response) => {
      this.userFunctionnalAttributes = response.data
    })
    this.initEvaluator()
    this.loadTreeDataSearchButton()
  },
  methods: {
    initEvaluator() {
      this.evaluatorConfig.getSubjectInfos = this.subjectInfosConfig.getSubjectInfos
      this.evaluatorConfig.userDisplayedAttrs = this.userDisplayedAttrs
    },
    // Chargement du niveau 0 du treeview pour le webcomponent esup-subject-search-button
    loadTreeDataSearchButton() {
      GroupService.search({
        context: this.context.contextKey,
        search: 1,
        subContexts: [],
      }).then((response) => {
        this.treeData = response.data
        this.treeData.forEach(element => this.initTreeNodeProperties(element))
        // Initialisation de la configuration du webcomponent esup-subject-search-button
        this.subjectSearchButtonConfig.treeGroupDatas = this.treeData
        this.subjectSearchButtonConfig.userDisplayedAttrs = this.userDisplayedAttrs
        this.subjectSearchButtonConfig.extendedAttrs = this.userFonctionalAttrs
        this.subjectSearchButtonConfig.getGroupMembers = id =>
          GroupService.userMembers(id).then((res) => {
            return res.data
          })
        this.subjectSearchButtonConfig.searchUsers = search =>
          UserService.search({
            context: this.context.contextKey,
            search,
            subContexts: [],
          }).then((res) => {
            return res.data
          })
        // Initialisation de la configuration du webcomponent esup-edit-evaluator
        this.editEvaluatorConfig.treeGroupDatas = this.treeData
        this.editEvaluatorConfig.userDisplayedAttrs = this.userDisplayedAttrs
        this.editEvaluatorConfig.userAttributes = this.userFunctionnalAttributes
        this.editEvaluatorConfig.operators = this.operatorTypeList
        this.editEvaluatorConfig.stringEvaluators = this.stringEvaluationModeList
        this.editEvaluatorConfig.getGroupMembers = id =>
          GroupService.userMembers(id).then((res) => {
            return res.data
          })
        this.editEvaluatorConfig.searchUsers = search =>
          UserService.search({
            context: this.context.contextKey,
            search,
            subContexts: [],
          }).then((res) => {
            return res.data
          })
        this.editEvaluatorConfig.getSubjectInfos = (keyType, keyId) => {
          return SubjectService.getSubjectInfos(keyType, keyId).then((res) => {
            if (res) {
              return res.data
            }
          })
        }
        this.setIsDatasLoad(true)
      })
    },
    // Mise à jour asynchrone des enfants du treeview
    loadTreeDataChildrenSearchButton(id) {
      return GroupService.search({
        context: this.context.contextKey,
        search: id,
        subContexts: [],
      }).then((response) => {
        response.data.forEach(element => this.initTreeNodeProperties(element))
        return response.data
      })
    },
    // Intialisation des propriétés d'un noeud de l'arbre
    initTreeNodeProperties(node) {
      if (node.children) {
        node.getChildren = () => this.loadTreeDataChildrenSearchButton(node.id)
      }
    },
    // Affichage des informations de la cible
    viewSubject(subject) {
      if (subject) {
        if (subject.subjectDTO && subject.subjectDTO.modelId) {
          this.subjectDetail.showSubjectModal(subject.subjectDTO.modelId)
        }
        else if (subject.modelId) {
          this.subjectDetail.showSubjectModal(subject.modelId)
        }
        else if (subject.keyId && subject.keyType) {
          this.subjectDetail.showSubjectModal(subject)
        }
      }
    },
    onModificationEvaluator(data) {
      this.onModificationEditEvaluator(data, this.$refs.editEvaluatorPermissionOnCtx.isValid())
    },
  },
}
</script>

<template>
  <h3 class="mt-3 mb-2">
    {{ $t('permissionOnContext.detail.title') }} &nbsp;
    <span
      v-has-role="'ROLE_ADMIN'"
      v-tooltip="$t('permission.advancedMode')"
      class="hand"
      :class="{ disabled: !permissionAdvanced }"
      @click="changePermissionAdvanced()"
    ><i class="fa fa-graduation-cap" /></span>
  </h3>

  <SubjectDetail ref="subjectDetail" />

  <div v-show="!addPerm" aria-labelledby="addPermissionOnContextLabel" aria-hidden="true">
    <form name="editPermOnCtxForm" role="form">
      <div class="header">
        <h4 id="myPermissionOnContextLabel">
          {{ $t('permissionOnContext.home.createOrEditLabel') }}
        </h4>
      </div>
      <div class="body">
        <div class="form-group">
          <label for="permRole">{{ $t('permissionOnContext.role') }}</label>
          <select id="permRole" v-model="permission.role" class="form-select" name="permRole">
            <option v-for="role in availableRoles" :key="role.id" :value="role.name">
              {{ $t(role.label) }}
            </option>
          </select>
        </div>

        <div v-show="permissionAdvanced" class="form-group">
          <label>{{ $t('permissionOnContext.evaluator') }}</label>
          <esup-edit-evaluator
            ref="editEvaluatorPermissionOnCtx"
            .evaluator="permission.evaluator"
            .config="editEvaluatorConfig"
            .onModification="onModificationEvaluator"
            .onSubjectClicked="(subject) => viewSubject(subject)"
          />
          <div v-if="editEvaluatorError" class="invalid-feedback d-block">
            {{ $t('entity.validation.required') }}
          </div>
        </div>

        <div v-show="!permissionAdvanced && !isAdvancedEvaluator(permission.evaluator)" class="form-group">
          <label class="control-label">{{ $t('permissionOnContext.evaluatorsimple') }}</label>
          <esup-subject-search-button
            .searchId="'permCtxSubject'"
            .config="subjectSearchButtonConfig"
            .onSelection="updateSelectedSubject"
          />
          <ul class="list-group list-unstyled">
            <li v-for="subject in selectedSubjects" :key="subject.keyId" class="list-group-item">
              <esup-subject-infos .subject="subject" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
                <a v-tooltip="$t('manager.publish.targets.remove')" href="" @click.prevent="removeFromSelectedSubjects(subject)"><i class="far fa-trash-can text-danger" /></a>&nbsp;
              </esup-subject-infos>
            </li>
          </ul>
          <div v-if="!areSubjectsSelected()" class="invalid-feedback d-block">
            {{ $t('entity.validation.required') }}
          </div>
        </div>

        <div v-show="!permissionAdvanced && isAdvancedEvaluator(permission.evaluator)" class="form-group">
          <span>{{ $t('evaluators.forAdvancedOnly') }}</span>
        </div>
      </div>

      <div class="footer">
        <button type="button" class="btn btn-default btn-outline-dark me-1" @click="clearPermission()">
          <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
        </button>
        <button
          type="button"
          :disabled="
            !permission.role
              || (permissionAdvanced && editEvaluatorError)
              || (!permissionAdvanced && (!areSubjectsSelected || isAdvancedEvaluator(permission.evaluator)))
          "
          class="btn btn-primary"
          @click="createPermission()"
        >
          <span class="fas fa-floppy-disk" />&nbsp;<span>{{ $t('entity.action.save') }}</span>
        </button>
      </div>
    </form>
  </div>

  <div v-show="addPerm">
    <button v-if="availableRoles.length > 0 && canEditCtxPerms" type="button" class="btn btn-primary" @click="addPermission()">
      <span class="fa fa-bolt" />
      <span>{{ $t('permission.home.createLabel') }}</span>
    </button>
    <div v-show="permissionAdvanced" class="table-responsive table-responsive-to-cards overflow-visible evaluators">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('permission.role') }}</th>
            <th>{{ $t('permission.evaluator') }}</th>
            <th class="action" />
          </tr>
        </thead>
        <tbody>
          <tr v-for="permission in permissions" :key="permission.id">
            <td :data-label="$t('permission.role')">
              {{ $t(getPermissionTypeLabel(permission.role)) }}
            </td>
            <td class="verylongtext" :data-label="$t('permission.evaluator')">
              <esup-evaluator
                .evaluator="permission.evaluator"
                .config="evaluatorConfig"
                .onSubjectClicked="(subject) => viewSubject(subject)"
              />
            </td>
            <td class="action">
              <button v-if="canEditCtxPerms" type="button" class="btn btn-primary me-1" @click="updatePermission(permission.id)">
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button v-if="canEditCtxPerms" type="button" class="btn btn-danger" @click="deletePermission(permission.id)">
                <span class="far fa-trash-can" />&nbsp;<span>{{ $t('entity.action.delete') }}</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-show="!permissionAdvanced" class="table-responsive table-responsive-to-cards overflow-visible evaluators">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>{{ $t('permission.role') }}</th>
            <th>{{ $t('permissionOnContext.evaluatorsimple') }}</th>
            <th class="action" />
          </tr>
        </thead>
        <tbody>
          <tr v-for="permission in permissions" :key="permission">
            <td :data-label="$t('permission.role')">
              {{ $t(getPermissionTypeLabel(permission.role)) }}
            </td>
            <td class="verylongtext" :data-label="$t('permissionOnContext.evaluatorsimple')">
              <esup-evaluator
                .evaluator="permission.evaluator"
                .simple="true"
                .config="evaluatorConfig"
                .onSubjectClicked="(subject) => viewSubject(subject)"
              />
            </td>
            <td class="action">
              <button
                v-if="!isAdvancedEvaluator(permission.evaluator) && canEditCtxPerms"
                type="button"
                class="btn btn-primary me-1"
                @click="updatePermission(permission.id)"
              >
                <span class="fas fa-pencil" />&nbsp;<span>{{ $t('entity.action.edit') }}</span>
              </button>
              <button
                v-if="!isAdvancedEvaluator(permission.evaluator) && canEditCtxPerms"
                type="button"
                class="btn btn-danger"
                @click="deletePermission(permission.id)"
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
