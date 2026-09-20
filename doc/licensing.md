# Licensing

The ESUP Publisher source code is licensed under Apache-2.0. The root `NOTICE` file is generated from Maven dependencies and is checked with `./mvnw notice:check`.

## Frontend notices

`src/main/webapp/public/NOTICE-frontend.txt` lists the complete npm production dependency tree, its declared license metadata, repository information when available, and the corresponding license texts. Vite copies this file to the frontend distribution, making it available at `/publisher/ui/NOTICE-frontend.txt` in a deployed application.

Regenerate the notice after changing `package.json` or `package-lock.json`:

```sh
npm ci
npm run notice:frontend
```

Verify that the committed notice matches the installed dependency tree:

```sh
npm run notice:frontend:check
```

The generator rejects dependencies with missing or unknown license data. Any exceptional package must be resolved deliberately before the notice can be regenerated.

## CKEditor

CKEditor 5 is distributed by CKSource under GPL v2 or later, with a commercial alternative. The project currently uses the GPL-distributed npm packages. Before upgrading CKEditor, confirm the distribution terms and configure the selected license key explicitly. A GPL deployment must retain the CKEditor notices and make the corresponding source available to recipients of the frontend distribution.
