describe('file-system controller tests', function () {

    beforeEach(angular.mock.module('fileApp'));

    var $controller, $httpBackend;
    var httpDataDirectory = [{
        fileName: 'C:\\',
        fullPath: 'C:\\',
        contentType: null,
        creationDate: 1468649064614,
        modificationDate: 1490949535029,
        fileSize: 8,
        directory: true
    }];
    var httpDataFile = [{
        fileName: 'testFile.txt',
        fullPath: 'C:\\testFile.txt',
        contentType: 'txt',
        creationDate: 1491208235125,
        modificationDate: 1491208235125,
        fileSize: 0,
        directory: false
    }];

    beforeEach(inject(function (_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
    }));

    it('loading root files (with mock http request)', function () {
        var controller = $controller('fileSystemCtrl');
        $httpBackend.when('GET', '/training/files').respond(httpDataDirectory);
        $httpBackend.flush();
        excpect(controller.files.length).toBeGreaterThan(0);
        expect(controller.files).toEqual(httpDataDirectory);
    });

    /*it('delete file (with mock http request)', function () {
        var controller = $controller('fileSystemCtrl');
        console.log('INITIAL' + controller.files.length);
        $httpBackend.when('GET', '/training/files').respond(httpDataFile);
        $httpBackend.when('DELETE', function (url) {
            console.log(url);
            return url.indexOf('/training/admin/delete') !== -1;
        }).respond(200);
        controller.deleteFile(httpDataFile[0]);
        $httpBackend.flush();

        console.log('AFTER EXEC ' + controller.files.length);
        console.log(controller.files[0]);
        console.log(httpDataFile[0]);
        console.log('INDEX ' + httpDataFile.indexOf(httpDataFile[0]));
        console.log('INDEX ' + controller.files.indexOf(httpDataFile[0]));

        expect(controller.files[0]).toEqual(httpDataFile[0]);
        expect(controller.files.length).toBe(0);
    });*/

    it('directory back (one element in current path)', function () {
        var controller = $controller('fileSystemCtrl');
        controller.currentPathElements = [httpDataDirectory.fileName];
        $httpBackend.when('GET', '/training/files').respond(httpDataDirectory);
        $httpBackend.flush();

        controller.directoryBack();
        expect(controller.files.length).toBe(1);
        expect(controller.files).toEqual(httpDataDirectory);
    });

    it('create directory (with mock http request)', function () {
        var newDirectory = [{
            fileName: 'new-dir',
            fullPath: 'C:\\new-dir',
            contentType: null,
            creationDate: 1468649064614,
            modificationDate: 1490949535029,
            fileSize: 8,
            directory: true
        }];
        var controller = $controller('fileSystemCtrl');
        controller.currentPathElements.push(httpDataDirectory[0].fileName);
        controller.newDirectoryName = newDirectory.fileName;

        $httpBackend.when('GET', '/training/files').respond(httpDataFile);
        $httpBackend.when('POST', '/training/admin/addDirectory').respond(200, newDirectory);
        $httpBackend.flush();

        controller.createDirectory();
        expect(controller.files.length).toBe(1);
    });

    it('form current path (empty)', function () {
        var controller = $controller('fileSystemCtrl');
        var result = controller.formCurrentPath();
        expect(controller.currentPathElements.length).toBe(0);
        expect(result).toEqual('');
    });

    it('form current path', function () {
        var controller = $controller('fileSystemCtrl');
        controller.currentPathElements.push(httpDataDirectory[0].fileName);
        var result = controller.formCurrentPath();
        expect(controller.currentPathElements.length).toBe(1);
        expect(result).toEqual(httpDataDirectory[0].fileName + '\\');
    });

    it('is logged user admin (admin permissions are present)', function () {
        var controller = $controller('fileSystemCtrl');
        controller.userRoles = [];
        controller.userRoles.push({authority: 'ADMIN'});
        var result = controller.isLoggedUserAdmin();
        expect(result).toBe(true);
    });

    it('is logged user admin (admin permissions aren\'t present)', function () {
        var controller = $controller('fileSystemCtrl');
        controller.userRoles = [];
        controller.userRoles.push({authority: 'GUEST'});
        var result = controller.isLoggedUserAdmin();
        expect(result).toBe(false);
    });
});