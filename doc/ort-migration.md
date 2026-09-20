# Migration vers OSS Review Toolkit (ORT)

Ce document est la checklist de migration de la production des notices de
licences vers [OSS Review Toolkit (ORT)](https://oss-review-toolkit.org/ort/).
Il concerne le projet Maven et le frontend npm conjointement. Ne pas modifier
le `NOTICE` ni supprimer l'outillage Maven existant avant la validation de la
baseline ORT.

## Objectif

Produire un unique `NOTICE` pour le WAR, couvrant les dependances Maven et npm
qui sont distribuees, avec les licences et copyrights issus des metadonnees et
des sources scannees par ORT.

Le perimetre retenu est volontairement conservateur : toutes les dependances
de production Maven et npm sont incluses. Les dependances de test, fournies par
le conteneur, ou de developpement uniquement sont exclues avec une justification
versionnee.

## Etat initial

- `NOTICE` est actuellement genere par `notice-maven-plugin` dans `pom.xml`.
- `etc/NOTICE.template` fournit son preambule et
  `etc/license-mappings.xml` corrige des licences Maven historiques.
- `package-lock.json` est versionne et doit rester la source de resolution npm.
- La CI controle aujourd'hui `./mvnw license:check` et
  `./mvnw notice:check`; aucune notice npm n'est produite.

## Choix techniques

- Executer une unique analyse ORT a la racine, avec les gestionnaires `Maven,NPM`.
- Utiliser l'image Docker officielle ORT, epinglee par digest. Les binaires ORT
  actuels requierent Java 25; ils ne doivent pas imposer ce JDK au build de
  l'application.
- Conserver les resultats ORT en artefacts de CI, mais ne pas les versionner.
- Generer `NOTICE` avec un template ORT local afin de conserver le preambule
  ESUP-Portail et un nom de fichier stable.
- Ne pas remplacer une licence par une resolution. Les corrections de
  metadonnees vont dans des curations, avec une justification tracable.

## Fichiers a introduire

| Fichier | Role |
| --- | --- |
| `.ort.yml` | Perimetre distribue, exclusions justifiees et choix de licences propres au depot. |
| `ort/config.yml` | Configuration ORT partagee par la CI, utilisee avec `ORT_CONFIG_DIR=$GITHUB_WORKSPACE/ort`. |
| `ort/templates/NOTICE.ftl` | Template `PlainTextTemplate` qui produit le `NOTICE` consolide. |
| `ort/license-classifications.yml` | Classification des licences pour la politique. |
| `ort/evaluator.rules.kts` | Regles de politique de licences et severites. |
| `ort/curations.yml` | Corrections factuelles de metadonnees de paquets. Initialement vide si aucune correction n'est connue. |
| `ort/resolutions.yml` | Exceptions documentees pour problemes ou vulnerabilites non corrigibles. Initialement vide si aucune exception n'est approuvee. |

Ajouter `ort-results/` au `.gitignore`. Versionner tous les fichiers de
configuration et le template, y compris lorsqu'ils sont vides.

### Perimetre de `.ort.yml`

Ajouter les exclusions suivantes, en gardant les commentaires expliquant le
mode de distribution :

```yaml
excludes:
  scopes:
    - pattern: "test"
      reason: "TEST_DEPENDENCY_OF"
      comment: "Dependencies used only by Maven tests."
    - pattern: "provided"
      reason: "PROVIDED_DEPENDENCY_OF"
      comment: "Dependencies provided by the runtime container."
    - pattern: "devDependencies"
      reason: "DEV_DEPENDENCY_OF"
      comment: "Dependencies used only to build, lint, or test the frontend."
```

Ne pas activer `analyzer.skip_excluded` lors de la premiere migration : les
dependances exclues restent ainsi visibles dans les rapports et l'exclusion peut
etre verifiee. L'activer seulement apres revue du perimetre.

## Execution locale de reference

Verifier d'abord les prerequis dans le conteneur ORT avec `ort requirements`.
Employer ensuite la meme image epinglee que celle de la CI. La syntaxe exacte
doit etre confirmee avec `ort --help` pour cette version avant de la figer.

```sh
ort -P ort.analyzer.enabledPackageManagers=Maven,NPM analyze -i . -o ort-results/analyzer
ort scan -i ort-results/analyzer/analyzer-result.yml -o ort-results/scan
ort evaluate -i ort-results/scan/scan-result.yml -o ort-results/evaluate
ort report -i ort-results/evaluate/evaluation-result.yml -o ort-results/report \
  --report-formats PlainTextTemplate,StaticHtml,WebApp \
  -O PlainTextTemplate=template.path=ort/templates/NOTICE.ftl
```

Le template doit ecrire `ort-results/report/NOTICE`. Les rapports `StaticHtml`
et `WebApp` sont des outils de revue; ils ne sont pas des livrables de
l'application.

## Plan de migration

1. Ajouter les fichiers ORT sans activer de blocage de politique.
2. Executer analyse, scan, evaluation et rapport avec une image ORT epinglee.
3. Revoir chaque licence inconnue, ambigue ou differente du `NOTICE` existant.
4. Ajouter une curation seulement pour corriger un fait verifiable; ajouter le
   lien vers la source et la raison dans son commentaire.
5. Revoir les exclusions `test`, `provided` et `devDependencies` avec les
   responsables de la distribution.
6. Generer le nouveau `NOTICE` et le relire juridiquement ou par le responsable
   conformite. Le changement initial est attendu: ORT deduplique et exprime les
   licences en SPDX.
7. Configurer le packaging pour inclure le `NOTICE` final dans le WAR, par
   exemple sous `META-INF/NOTICE`. Le `NOTICE` racine seul ne garantit pas sa
   presence dans l'archive.
8. Remplacer le controle Maven de notice par le controle ORT dans la CI.
9. Supprimer seulement apres la bascule validee `notice-maven-plugin`,
   `etc/NOTICE.template` et `etc/license-mappings.xml`. Conserver
   `maven-license-plugin`, qui controle les en-tetes des sources et repond a un
   besoin distinct.
10. Activer progressivement l'evaluator en echec de CI pour les nouvelles
    violations non approuvees.

## Integration CI

Ajouter un job ORT distinct du build Java et du job frontend. Le declencher lors
des modifications de `pom.xml`, `package.json`, `package-lock.json`, `.ort.yml`,
`ort/**`, `NOTICE` ou du workflow.

- Epinglez `oss-review-toolkit/ort-ci-github-action` par SHA et l'image ORT par
  digest; ne pas utiliser `main` ni `latest`.
- Configurez explicitement les etapes
  `cache-dependencies,analyzer,scanner,evaluator,reporter,upload-results`.
  Le scan est indispensable pour produire une notice fondee sur les sources.
- Restreignez l'analyse a `Maven,NPM` avec
  `-P ort.analyzer.enabledPackageManagers=Maven,NPM`.
- Publiez `ort-results/` comme artefact de CI pour permettre une revue des
  ecarts et des curations.
- Comparez le `NOTICE` produit avec le `NOTICE` versionne et echouez si le diff
  n'est pas vide. Une modification de dependance impose donc une notice revue.
- Initialement, n'echouez pas sur les violations de politique. Activez
  `fail-on: violations` une fois les classifications, curations et choix de
  licences stabilises.

Les caches de resultats de scan doivent inclure dans leur cle le digest de
l'image ORT, `pom.xml`, `package-lock.json` et `ort/**`.

## Regles de maintenance

- Ne jamais editer manuellement la partie tierce du `NOTICE` genere.
- Ne jamais utiliser une resolution pour masquer une licence incorrecte : elle
  ne modifie pas la notice. Corriger la metadonnee par curation ou remplacer la
  dependance.
- Toute curation ou resolution doit contenir une raison, un commentaire et une
  reference verifiable.
- Reecrire et relire le `NOTICE` apres chaque mise a jour d'ORT, de Maven ou de
  `package-lock.json`.
- Conserver `package-lock.json`; une resolution npm sans lockfile n'est pas une
  baseline de conformite reproductible.

## Documentation ORT

- [Usage et configuration](https://oss-review-toolkit.org/ort/docs/getting-started/usage)
- [Prerequis d'execution](https://oss-review-toolkit.org/ort/docs/getting-started/runtime-requirements)
- [Configuration de depot `.ort.yml`](https://oss-review-toolkit.org/ort/docs/configuration/ort-yml)
- [Curations de paquets](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)
- [Regles de l'evaluator](https://oss-review-toolkit.org/ort/docs/configuration/evaluator-rules)
- [Templates de rapport et notices](https://oss-review-toolkit.org/ort/docs/configuration/reporter-templates)
- [Action GitHub officielle](https://github.com/oss-review-toolkit/ort-ci-github-action)
