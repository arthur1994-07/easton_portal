module.exports = {
	extends: [
		// add more generic rulesets here, such as:
		'eslint:recommended',
		'plugin:vue/vue3-recommended',
		// 'plugin:vue/recommended' // Use this if you are using Vue.js 2.x.
	],
	rules: {
		// override/add rules settings here, such as:
		"indent": ["error", "tab"], // enforce tabs in script and js files
		'vue/no-unused-vars': 'error',
		"vue/html-indent": ["error", "tab"],  // enforce tabs in template

		// below are off the option
		"vue/require-prop-types":0,
		"vue/require-default-prop" : 0,
		"vue/v-slot-style" : 0,
		"vue/singleline-html-element-content-newline" : 0,
		"vue/attribute-hyphenation" : 0,
		"vue/v-on-event-hyphenation" : 0,
		"vue/max-attributes-per-line": 0,
		"vue/first-attribute-linebreak": 0
	}
}