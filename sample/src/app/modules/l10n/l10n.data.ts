export const DATA = [
    {
        key: 'application.title',
        source: 'Welcome to Singleton Angular sample application!',
        params: []
    },
    {
        key: 'demo.string.description',
        source: 'Singleton Angular client supports both {0} and {1}.',
        variables: ['i18n', 'l10n'],
        params: ['i18n', 'l10n']
    },
    {
        key: 'demo.plural.users',
        source: '{0, plural, one {Singleton Angular client has a user.} other {Singleton Angular client has # users.}}',
        comment: 'plural',
        variables: [ 1 ],
        params: [ 1, 'plural' ]
    },
    {
        key: 'demo.plural.users',
        source: '{0, plural, one {Singleton Angular client has a user.} other {Singleton Angular client has # users.}}',
        comment: 'plural',
        variables: [ 10 ],
        params: [ 10, 'plural' ]
    }
];
