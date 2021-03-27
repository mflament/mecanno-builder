import nodeResolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import replace from '@rollup/plugin-replace';

const env = "production";

const plugins = [
  nodeResolve(),
  replace({
    'process.env.NODE_ENV': JSON.stringify(env)
  }),
  commonjs()
];

const output = (file) => ({
  file: file,
  format: 'esm',
  sourcemap: true,
  exports: "named"
});

export default [
  {
    input: ['node_modules/react-toastify/dist/react-toastify.esm.js'],
    external: ['react', 'react-dom'],
    output: output('dist/react-toastify/react-toastify.js'),
    plugins: plugins
  },
  {
    input: ['node_modules/react-sortable-hoc/dist/react-sortable-hoc.esm.js'],
    external: ['react', 'react-dom'],
    output: output('dist/react-sortable-hoc/react-sortable-hoc.esm.js'),
    plugins: plugins
  }];