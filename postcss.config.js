module.exports = {
    plugins: [
        require("autoprefixer")({
            overrideBrowserslist: [
                "Android 4.1",
                "iOS 7.1",
                "Chrome > 31",
                "ff > 31",
                "ie >= 8"
            ],
            grid: true
        }),
        require("cssnano")({
            preset: [
                "default",
                {
                    parser: require("postcss-safe-parser"),
                    discardComments: { removeAll: true },
                    safe: true,
                    mergeLonghand: true,
                    autoprefixer: false
                }
            ]
        })
    ]
};
