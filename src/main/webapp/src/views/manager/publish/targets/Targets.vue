<script>
import GroupService from '@/services/entities/group/GroupService.js'
import SubjectService from '@/services/params/SubjectService.js'
import UserService from '@/services/user/UserService.js'
import CommonUtils from '@/services/util/CommonUtils.js'
import SubjectDetail from '@/views/entities/subject/SubjectDetail.vue'

export default {
  name: 'Targets',
  components: {
    SubjectDetail,
  },
  inject: ['publisher', 'targets', 'setTargets', 'classifications', 'contentData'],
  data() {
    return {
      subjectSearchButtonConfig: {
        treeGroupDatas: [],
        userDisplayedAttrs: [],
        extendedAttrs: [],
        searchUsers: null,
        getGroupMembers: null,
        lang: this.$store.getters.getLanguage,
      },
      subjectInfosConfig: {
        getSubjectInfos: null,
        resolveKey: true,
        userDisplayedAttrs: null,
      },
    }
  },
  computed: {
    userDisplayedAttrs() {
      return SubjectService.getUserDisplayedAttrs()
    },
    userFonctionalAttrs() {
      return SubjectService.getUserFonctionalAttrs()
    },
  },
  mounted() {
    this.subjectDetail = this.$refs.subjectDetail
  },
  created() {
    // Si aucun publisher de saisie, on redirige vers la page Publisher
    if (this.publisher === null || this.publisher === undefined) {
      this.$router.push({ path: 'publisher' })
    }
    else {
      if (
        (this.targets === null || this.targets === undefined || CommonUtils.equals([], this.targets))
        && this.contentData !== null
        && this.contentData.targets
      ) {
        this.setTargets(Array.from(this.contentData.targets.map(target => target.subject)))
      }
      this.initDatas()
    }
  },
  methods: {
    initDatas() {
      this.initSubjectInfosConfig()
      this.loadTreeDataSearchButton()
    },
    // Configuration du webcomponent esup-subject-infos
    initSubjectInfosConfig() {
      this.subjectInfosConfig.userDisplayedAttrs = this.userDisplayedAttrs
      this.subjectInfosConfig.getSubjectInfos = (keyType, keyId) => {
        return SubjectService.getSubjectInfos(keyType, keyId).then((res) => {
          if (res) {
            return res.data
          }
        })
      }
    },
    // Configuration du web-component esup-subject-search-button
    loadTreeDataSearchButton() {
      GroupService.search({
        context: this.publisher.contextKey,
        search: 1,
        subContexts: this.classifications,
      }).then((response) => {
        const treeData = response.data
        treeData.forEach((element) => {
          if (element.children) {
            element.getChildren = () => this.loadTreeDataChildrenSearchButton(element.id)
          }
        })
        this.subjectSearchButtonConfig.treeGroupDatas = treeData
        this.subjectSearchButtonConfig.userDisplayedAttrs = this.userDisplayedAttrs
        this.subjectSearchButtonConfig.extendedAttrs = this.userFonctionalAttrs
        this.subjectSearchButtonConfig.getGroupMembers = id =>
          GroupService.userMembers(id).then((response) => {
            return response.data
          })
        this.subjectSearchButtonConfig.searchUsers = search =>
          UserService.search({
            context: this.publisher.contextKey,
            search,
            subContexts: [],
          }).then((response) => {
            return response.data
          })
      })
    },
    // Mise à jour asynchrone des enfants du treeview
    loadTreeDataChildrenSearchButton(id) {
      return GroupService.search({
        context: this.publisher.contextKey,
        search: id,
        subContexts: this.classifications,
      }).then((response) => {
        response.data.forEach((element) => {
          if (element.children) {
            element.getChildren = () => this.loadTreeDataChildrenSearchButton(element.id)
          }
        })
        return response.data
      })
    },
    remove(subject) {
      const updatedTargets = this.targets.filter((element) => {
        return element.modelId !== subject.modelId
      })
      this.setTargets(updatedTargets)
    },
    // Affichage des informations de la cible
    viewSubject(target) {
      if (target) {
        if (target.subjectDTO && target.subjectDTO.modelId) {
          this.subjectDetail.showSubjectModal(target.subjectDTO.modelId)
        }
        else if (target.modelId) {
          this.subjectDetail.showSubjectModal(target.modelId)
        }
        else if (target.keyId && target.keyType) {
          this.subjectDetail.showSubjectModal(target)
        }
      }
      return false
    },
    // Mise à jour du sujet séléctionné
    updateSelectedSubject(datas) {
      datas.forEach((newVal) => {
        if (newVal !== null && 'modelId' in newVal && !this.containsSubcriber(newVal.modelId)) {
          const newTargets = this.targets ? Array.from(this.targets) : []
          newTargets.push(newVal)
          this.setTargets(newTargets)
        }
      })
    },
    containsSubcriber(subjectKey) {
      if (this.targets && this.targets.length > 0) {
        return this.targets.some(target => target.modelId.keyId === subjectKey.keyId)
      }
      return false
    },
  },
}
</script>

<template>
  <div class="publish-target">
    <h3>{{ $t('manager.publish.targets.title') }}</h3>

    <SubjectDetail ref="subjectDetail" />

    <div class="form-group">
      <label class="control-label" for="targets">{{ $t('manager.publish.targets.selected') }}</label>
      <div id="targets">
        <esup-subject-search-button
          .search-id="'targetSubject'"
          .withExtended="false"
          .multiSelection="true"
          .config="subjectSearchButtonConfig"
          .onSelection="updateSelectedSubject"
        />

        <ul class="list-group list-unstyled">
          <li v-for="subject in targets" :key="subject" class="list-group-item">
            <esup-subject-infos .subject="subject" .config="subjectInfosConfig" .onSubjectClicked="() => viewSubject(subject)">
              <a v-tooltip="$t('manager.publish.targets.remove')" href="" @click.prevent="remove(subject)">
                <i class="far fa-trash-can text-danger" /> </a>&nbsp;
            </esup-subject-infos>
          </li>
        </ul>
      </div>
    </div>

    <div class="text-center">
      <div class="btn-group" role="group">
        <router-link v-slot="{ navigate }" to="content" custom>
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-arrow-left" />&nbsp;<span>{{ $t('entity.action.back') }}</span>
          </button>
        </router-link>
        <router-link v-slot="{ navigate }" to="/home" custom>
          <button type="button" class="btn btn-default btn-outline-dark btn-nav" @click="navigate">
            <span class="fas fa-times" />&nbsp;<span>{{ $t('entity.action.cancel') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>
