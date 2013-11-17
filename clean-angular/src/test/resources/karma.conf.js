module.exports = function(config) {
    config.set({
        basePath: '../../../',
        files: [
            'src/main/webapp/lib/angular/angular.js',
            'src/main/webapp/lib/**/*.js',
            'src/test/resources/lib/**/**.js',
            'src/main/webapp/js/**/*.js',
            'src/test/resources/unit/**/*.js'
        ],
        exclude: [
            'src/main/webapp/lib/angular/angular-loader.js',
            'src/main/webapp/lib/angular/*.min.js'
        ],
        reporters: ['progress'],
        autoWatch: true,
        frameworks: ['jasmine'],
        browsers: ['Chrome'],
        plugins: [
            'karma-junit-reporter',
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-jasmine'
        ],
        junitReporter: {
            outputFile: 'TEST-karma.xml'
        }
    });
};
