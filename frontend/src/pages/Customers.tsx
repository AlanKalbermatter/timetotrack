import { useState } from "react";
import { errorMessage } from "../api/client";
import { createCustomer, deleteCustomer, listCustomers } from "../api/customers";
import ConfirmButton from "../components/ConfirmButton";
import NewCustomerModal from "../components/modals/NewCustomerModal";
import PageCard from "../components/PageCard";
import { AsyncContent, ErrorMessage } from "../components/states";
import { cellClass, primaryButtonClass, rowClass, tableClass, theadClass } from "../components/ui";
import { useAsync } from "../hooks/useAsync";

/** Customers list with create and delete (deleting a customer that has projects is rejected by the API). */
const Customers = () => {
    const customers = useAsync(listCustomers);
    const [showModal, setShowModal] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);

    const remove = async (id: number) => {
        setActionError(null);
        try {
            await deleteCustomer(id);
            customers.reload();
        } catch (err) {
            setActionError(errorMessage(err));
        }
    };

    return (
        <PageCard
            title="Customers"
            action={
                <button type="button" onClick={() => setShowModal(true)} className={primaryButtonClass}>
                    + New Customer
                </button>
            }
        >
            {actionError && (
                <div className="mb-4">
                    <ErrorMessage message={actionError} />
                </div>
            )}
            <AsyncContent state={customers} isEmpty={(list) => list.length === 0}
                          emptyMessage="No customers yet. Add one to start creating projects.">
                {(list) => (
                    <div className="overflow-x-auto">
                        <table className={tableClass}>
                            <thead className={theadClass}>
                                <tr>
                                    <th className={cellClass}>Name</th>
                                    <th className={`${cellClass} text-right`}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {list.map((customer) => (
                                    <tr key={customer.id} className={rowClass}>
                                        <td className={cellClass}>{customer.name}</td>
                                        <td className={`${cellClass} text-right`}>
                                            <ConfirmButton onConfirm={() => remove(customer.id)} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </AsyncContent>
            {showModal && (
                <NewCustomerModal
                    onClose={() => setShowModal(false)}
                    onSave={async (name) => {
                        await createCustomer(name);
                        customers.reload();
                    }}
                />
            )}
        </PageCard>
    );
};

export default Customers;
