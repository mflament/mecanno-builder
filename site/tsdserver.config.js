const path = require('path');

/**
 * @type {import("tsdserver").Options}
 */
module.exports = {
  directories: ['assets', 'dist'],
  debug: true,
  updateJSImport: {
    moduleResolver: {
      'react-toastify': 'react-toastify/react-toastify.esm.js',
      'react': 'react/source.development.js',
      'react-dom': 'react-dom/source.development.js',
      'react-sortable-hoc': 'react-sortable-hoc/react-sortable-hoc.esm.js'
    }
  }

};
