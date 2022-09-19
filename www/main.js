var exec = require('cordova/exec');

//Metodos Impressora
exports.checarImpressora = function (success, error) {
    exec(success, error, 'MainActivity', 'checarImpressora');
};
exports.imprimir = function (params, success, error) {
    exec(success, error, 'MainActivity', 'imprimir', [params]);
};
exports.impressoraOutput = function (params, success, error) {
    exec(success, error, 'MainActivity', 'impressoraOutput', [params]);
};

//Metodo Beep
exports.beep = function (success, error) {
    exec(success, error, 'MainActivity', 'beep');
};

//Metodos Led
exports.ledOn = function (success, error) {
    exec(success, error, 'MainActivity', 'ledOn');
};
exports.ledOff = function (success, error) {
    exec(success, error, 'MainActivity', 'ledOff');
};
exports.ledRedOn = function (success, error) {
    exec(success, error, 'MainActivity', 'ledRedOn');
};
exports.ledBlueOn = function (success, error) {
    exec(success, error, 'MainActivity', 'ledBlueOn');
};
exports.ledGreenOn = function (success, error) {
    exec(success, error, 'MainActivity', 'ledGreenOn');
};
exports.ledOrangeOn = function (success, error) {
    exec(success, error, 'MainActivity', 'ledOrangeOn');
};
exports.ledRedOff = function (success, error) {
    exec(success, error, 'MainActivity', 'ledRedOff');
};
exports.ledBlueOff = function (success, error) {
    exec(success, error, 'MainActivity', 'ledBlueOff');
};
exports.ledGreenOff = function (success, error) {
    exec(success, error, 'MainActivity', 'ledGreenOff');
};
exports.ledOrangeOff = function (success, error) {
    exec(success, error, 'MainActivity', 'ledOrangeOff');
};




exports.leitorCodigo1 = function (tipoCode, success, error) {
    exec(success, error, 'MainActivity', 'leitorCodigo1', [tipoCode]);
};

exports.leitorCodigoV2 = function (success, error) {
    exec(success, error, 'MainActivity', 'leitorCodigoV2');
};

exports.leitorNfcGedi = function (success, error) {
    exec(success, error, 'MainActivity', 'leitorNfcGedi');
};

exports.leitorNfcId = function (success, error) {
    exec(success, error, 'MainActivity', 'leitorNfcId');
};

exports.setSmartCardPowerOff = function (success, error) {
    exec(success, error, 'MainActivity', 'setSmartCardPowerOff');
};
exports.checkISmart = function (success, error) {
    exec(success, error, 'MainActivity', 'checkISmart');
};
exports.onICL = function (success, error) {
    exec(success, error, 'MainActivity', 'onICL');
};
exports.offICL = function (success, error) {
    exec(success, error, 'MainActivity', 'offICL');
};
exports.lerCartao = function (success, error) {
    exec(success, error, 'MainActivity', 'lerCartao');
};
exports.ativarLeituraICL = function (success, error) {
    exec(success, error, 'MainActivity', 'ativarLeituraICL');
};
exports.leitorNfcId = function (success, error) {
    exec(success, error, 'MainActivity', 'leitorNfcId');
};
exports.teset = function (success, error) {
    exec(success, error, 'MainActivity', 'teste');
};


