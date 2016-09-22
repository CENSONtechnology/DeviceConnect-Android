
var main = (function(parent, global) {

    function init() {
        util.init(function(name, json) {
            createSupportApis(json);
        });
    }
    parent.init = init;


    function onChangeValue(nav, name) {
        var elem = document.forms[nav];
        elem['t_' + name].value = elem[name].value;
    }
    parent.onChangeValue = onChangeValue;

    function isHiddenParam(name) {
        return name == 'deviceconnect.method' || name == 'deviceconnect.type' || name == 'deviceconnect.path';
    }

    function createBody(nav) {
        var data = [];

        data.push("accessToken=" + util.getAccessToken());

        var formElem = document.forms[nav];
        for (var key in formElem) {
            var elem = formElem[key];
            if (elem && elem.tagName) {
                if (elem.tagName.toLowerCase() == 'input') {
                    if (isHiddenParam(elem.name)) {
                        // 隠しパラメータ
                    } else if (elem.type == 'file') {
                        // どうするべきか検討
                    } else if (elem.name.indexOf('t_') != 0) {
                        if (elem.value.length != 0) {
                            data.push(elem.name + "=" + encodeURIComponent(elem.value));
                        }
                    }
                } else if (elem.tagName.toLowerCase() == 'select') {
                    data.push(elem.name + "=" + encodeURIComponent(elem.value));
                }
            }
        }

        return data;
    }

    function createFormData(nav) {
        var formData = new FormData();

        formData.append('accessToken', util.getAccessToken());

        var formElem = document.forms[nav];
        for (var key in formElem) {
            var elem = formElem[key];
            if (elem && elem.tagName) {
                if (elem.tagName.toLowerCase() == 'input') {
                    if (isHiddenParam(elem.name)) {
                        // 隠しパラメータ
                    } else if (elem.type == 'file') {
                        formData.append(elem.name, elem.files[0]);
                    } else if (elem.name.indexOf('t_') != 0) {
                        if (elem.value.length != 0) {
                            formData.append(elem.name, elem.value);
                        }
                    }
                } else if (elem.tagName.toLowerCase() == 'select') {
                    formData.append(elem.name, elem.value);
                }
            }
        }
        return formData;
    }

    function onSendRequest(nav) {
        var formElem = document.forms[nav];

        var method = formElem['deviceconnect.method'].value;
        var path = formElem['deviceconnect.path'].value;
        var xType = formElem['deviceconnect.type'].value;
        var body = null;

        if (xType == 'event') {
            var uri = "http://localhost:4035" + path.toLowerCase() + "?" + createBody(nav).join('&');

            setRequestText(nav, createRequest(method + " " + path));

            if (method == 'PUT') {
                dConnect.addEventListener(uri, function(json) {
                    setEventText(nav, createEvent(util.formatJSON(json)));
                }, function(json) {
                    setResponseText(nav, createResponse(util.formatJSON(JSON.stringify(json))));
                }, function(errorCode, errorMessage) {
                    setResponseText(nav, createResponse("errorCode=" + errorCode + " errorMessage=" + errorMessage));
                });
            } else {
                dConnect.removeEventListener(uri, function(json) {
                    setResponseText(nav, createResponse(util.formatJSON(JSON.stringify(json))));
                }, function(errorCode, errorMessage) {
                    setResponseText(nav, createResponse("errorCode=" + errorCode + " errorMessage=" + errorMessage));
                });
            }
        } else {
            if (method == 'GET' || method == 'DELETE') {
                path = path + "?" + createBody(nav).join('&');
            } else {
                body = createFormData(nav);
            }

            setRequestText(nav, createRequest(method + " " + path + "<br><br>" + body));

            util.sendRequest(method, util.getUri(path), body, function(status, response) {
                if (status == 200) {
                    setResponseText(nav, createResponse(util.formatJSON(response)));
                } else {
                    setResponseText(nav, createResponse("Http Status: " + status + "<br><br>" + response));
                }
            });
        }
    }
    parent.onSendRequest = onSendRequest;

    function setRequestText(nav, requestText) {
        document.getElementById(nav + '_request').innerHTML = requestText;
    }

    function setResponseText(nav, responseText) {
        document.getElementById(nav + '_response').innerHTML = responseText;
    }

    function setEventText(nav, eventText) {
        document.getElementById(nav + '_event').innerHTML = eventText;
    }

    function createDConnectPath(path) {
        return '/gotapi/' + util.getProfile() + path;
    }

    function createTextParam(name, value) {
        var data = {
            'name' : name,
            'value' : value
        };
        return util.createTemplate('param_text', data);
    }

    function createFileParam(name) {
        var data = {
            'name' : name
        };
        return util.createTemplate('param_file', data);
    }

    function createNumberParam(name, value) {
        var data = {
            'name' : name,
            'value' : value
        };
        return util.createTemplate('param_number', data);
    }

    function createSelectParam(name, list) {
        var text = "";
        for (var i = 0; i < list.length; i++) {
            text += '<option value="' + list[i] + '">' + list[i] + '</option>';
        }
        var data = {
            'name' : name,
            'value' : text
        };
        return util.createTemplate('param_select', data);
    }

    function createSliderParam(nav, name, min, max, step) {
        var data = {
            'nav' : nav,
            'name' : name,
            'value' : (max + min) / 2.0,
            'step' : step,
            'min' : '' + min,
            'max' : '' + max
        };
        return util.createTemplate('param_slider', data);
    }

    function createRequest(body) {
        var data = {
            'body' : body
        };
        return util.createTemplate('request', data);
    }

    function createResponse(body) {
        var data = {
            'body' : body
        };
        return util.createTemplate('response', data);
    }

    function createEvent(body) {
        var data = {
            'body' : body
        };
        return util.createTemplate('event', data);
    }

    function createParams(nav, params) {
        var contentHtml = "";
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param.type) {
            case 'string':
                if (('enum' in param)) {
                    contentHtml += createSelectParam(param.name, param.enum);
                } else {
                    if (param.name == 'serviceId') {
                        contentHtml += createTextParam(param.name, util.getServiceId());
                    } else {
                        contentHtml += createTextParam(param.name, '');
                    }
                }
                break;
            case 'array':
                contentHtml += createTextParam(param.name, '');
                break;
            case 'integer':
                if (('enum' in param)) {
                    contentHtml += createSelectParam(param.name, param.enum);
                } else if (('minimum' in param) && ('maximum' in param)) {
                    contentHtml += createSliderParam(nav, param.name, param.minimum, param.maximum, 1);
                } else {
                    contentHtml += createNumberParam(param.name, 0);
                }
                break;
            case 'number':
                if (('enum' in param)) {
                    contentHtml += createSelectParam(param.name, param.enum);
                } else if (('minimum' in param) && ('maximum' in param)) {
                    contentHtml += createSliderParam(nav, param.name, param.minimum, param.maximum, 0.01);
                } else {
                    contentHtml += createNumberParam(param.name, 0);
                }
                break;
            case 'file':
                contentHtml += createFileParam(param.name);
                break;
            default:
                console.log("Error: " + param.type);
                break;
            }
        }
        return contentHtml;
    }

    function createParameter(method, path, xType, params) {
        var nav = method + '_' + path;
        var data = {
            'nav' : nav,
            'method' : method.toUpperCase(),
            'path' : createDConnectPath(path),
            'xtype' : xType,
            'content' : createParams(nav, params)
        };
        return util.createTemplate('param', data);
    }

    function createCommand(method, path, param) {
        var data = {
            'title': method.toUpperCase() + ' ' + createDConnectPath(path),
            'nav' : method + '_' + path,
            'content' : createParameter(method, path, param['x-type'], param.parameters)
        };
        return util.createTemplate('command', data);
    }

    function createSupportMethod(path, data) {
        var contentHtml = "";
        for (var method in data) {
            contentHtml += createCommand(method, path, data[method]);
        }
        return contentHtml;
    }

    function createSupportPath(paths) {
        var contentHtml = "";
        for (var path in paths) {
            contentHtml += createSupportMethod(path, paths[path]);
        }
        return contentHtml;
    }

    function createSupportApis(json) {
        var profile = util.getProfile().toLowerCase();
        if (json.supportApis) {
            for (var p in json.supportApis) {
                if (profile == p.toLowerCase()) {
                    document.getElementById('main').innerHTML = createSupportPath(json.supportApis[p].paths);
                    return;
                }
            }
            alert(profile + 'プロファイルが見つかりません。');
        } else {
            alert('古いプラグインのために、このサービスは確認することができません。');
        }
    }

    return parent;
})(main || {}, this.self || global);
