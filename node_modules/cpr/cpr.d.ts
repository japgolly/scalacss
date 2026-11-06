/**
 * Nodejs cp -R
 */
declare module "cpr" {
    function cpr (from: string, to: string, options?: cpr.CprOptions, callback?: (err: any, files: string[]) => void): void;
    namespace cpr {
        interface CprOptions {
            deleteFirst?: boolean;
            overwrite?: boolean;
            confirm?: boolean;
            filter?: RegExp | Function;
        }
    }
    export = cpr;
}
