import { listUsers } from "../api/users";
import PageCard from "../components/PageCard";
import { AsyncContent } from "../components/states";
import { cellClass, rowClass, tableClass, theadClass } from "../components/ui";
import { useAsync } from "../hooks/useAsync";

/** Read-only team directory. */
const Users = () => {
    const users = useAsync(listUsers);

    return (
        <PageCard title="Team">
            <AsyncContent state={users} isEmpty={(list) => list.length === 0} emptyMessage="No teammates yet.">
                {(list) => (
                    <div className="overflow-x-auto">
                        <table className={tableClass}>
                            <thead className={theadClass}>
                                <tr>
                                    <th className={cellClass}>Name</th>
                                    <th className={cellClass}>Username</th>
                                    <th className={cellClass}>Email</th>
                                </tr>
                            </thead>
                            <tbody>
                                {list.map((user) => (
                                    <tr key={user.id} className={rowClass}>
                                        <td className={cellClass}>{user.fullName}</td>
                                        <td className={cellClass}>{user.username}</td>
                                        <td className={cellClass}>{user.email}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </AsyncContent>
            <p className="mt-4 text-xs text-gray-500 dark:text-gray-400">Teammates join by creating an account on the sign-up page.</p>
        </PageCard>
    );
};

export default Users;
