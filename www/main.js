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

//Metodo Ismart
exports.checkISmart = function (success, error) {
    exec(success, error, 'MainActivity', 'checkISmart');
};
//Método Contactless
exports.ativarLeituraICL = function (success, error) {
    exec(success, error, 'MainActivity', 'ativarLeituraICL');
};
//Clisitef
exports.pagamento = function (params, success, error) {
    exec(success, error, 'MainActivity', 'pagamento', [params]);
};


