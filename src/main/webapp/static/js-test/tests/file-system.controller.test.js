describe('file-system controller tests', function() {

    beforeEach(angular.mock.module('fileApp', ['ui.router']));

    var httpData = [{
        fileName : 'C:\\',
        fullPath : 'C:\\',
        contentType : null,
        creationDate : 1468649064614,
        modificationDate : 1490949535029,
        fileSize : 8,
        directory : true}];

    beforeEach(inject(function(_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('/training/files').respond({
            files: httpData
        });
    }));

    it('loading root files (with mock http request)', function () {
        var moviesController = $controller('fileSystemCtrl');
        $httpBackend.flush();
        expect(moviesController.files).toEqual([httpData]);
    });

});