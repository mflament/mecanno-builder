const gulp = require('gulp');
const del = require('del');
const less = require('gulp-less');
const rollup = require('rollup');
const loadConfigFile = require('rollup/dist/loadConfigFile');
const path = require('path');

async function run_rollup(conf) {
    const { options, warnings } = await loadConfigFile(path.resolve(__dirname, conf), { format: 'es' });
    console.log(`We currently have ${warnings.count} warnings`);
    warnings.flush();
    for (const option of options) {
        const bundle = await rollup.rollup(option);
        await Promise.all(option.output.map(bundle.write));
    }
}

function build_source() {
    return run_rollup('rollup.config.js');
}

function build_dev_modules() {
    return run_rollup('rollup.config.esm.js');
}

function clean() {
    return del(['dist']);
}

function css() {
    return gulp.src('less/*.less').pipe(less()).pipe(gulp.dest('dist/css'));
}

function react_toastify() {
    return gulp.src('node_modules/react-toastify/dist/ReactToastify.min.css').pipe(gulp.dest('dist/react-toastify'));
}

function watch() {
    gulp.watch('assets/**', assets);
    gulp.watch('less/*.less', css);
}

function assets() {
    return gulp.src('assets/**').pipe(gulp.dest('dist/'));
}

gulp.task('clean', clean);

gulp.task('assets', gulp.parallel(gulp.series(assets, react_toastify,), css));

gulp.task('dev-modules', build_dev_modules);

gulp.task('watch', gulp.series(clean, gulp.task('assets'), watch));

gulp.task('release', gulp.series(clean, gulp.parallel(gulp.task('assets'), build_source)));

gulp.task('default', gulp.task('watch'));
