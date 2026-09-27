import { useState } from "react";
import { errorMessage } from "../api/client";
import { listCustomers } from "../api/customers";
import { createProject, deleteProject, listProjects } from "../api/projects";
import ConfirmButton from "../components/ConfirmButton";
import NewProjectModal from "../components/modals/NewProjectModal";
import PageCard from "../components/PageCard";
import { AsyncContent, ErrorMessage } from "../components/states";
import { cellClass, primaryButtonClass, rowClass, tableClass, theadClass } from "../components/ui";
import { useAsync } from "../hooks/useAsync";

const Projects = () => {
    const projects = useAsync(listProjects);
    const customers = useAsync(listCustomers);
    const [showModal, setShowModal] = useState(false);
    const [actionError, setActionError] = useState<string | null>(null);
    const hasCustomers = (customers.data?.length ?? 0) > 0;

    const remove = async (id: number) => {
        setActionError(null);
        try {
            await deleteProject(id);
            projects.reload();
        } catch (err) {
            setActionError(errorMessage(err));
        }
    };

    return (
        <PageCard
            title="Projects"
            action={
                <button type="button" onClick={() => setShowModal(true)} disabled={!hasCustomers}
                        title={hasCustomers ? undefined : "Create a customer first"} className={primaryButtonClass}>
                    + New Project
                </button>
            }
        >
            {actionError && (
                <div className="mb-4">
                    <ErrorMessage message={actionError} />
                </div>
            )}
            <AsyncContent state={projects} isEmpty={(list) => list.length === 0}
                          emptyMessage={hasCustomers ? "No projects yet." : "Create a customer first, then add its projects here."}>
                {(list) => (
                    <div className="overflow-x-auto">
                        <table className={tableClass}>
                            <thead className={theadClass}>
                                <tr>
                                    <th className={cellClass}>Project</th>
                                    <th className={cellClass}>Customer</th>
                                    <th className={`${cellClass} text-right`}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {list.map((project) => (
                                    <tr key={project.id} className={rowClass}>
                                        <td className={cellClass}>{project.name}</td>
                                        <td className={cellClass}>{project.customerName}</td>
                                        <td className={`${cellClass} text-right`}>
                                            <ConfirmButton onConfirm={() => remove(project.id)} />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </AsyncContent>
            {showModal && customers.data && (
                <NewProjectModal
                    customers={customers.data}
                    onClose={() => setShowModal(false)}
                    onSave={async (name, customerId) => {
                        await createProject(name, customerId);
                        projects.reload();
                    }}
                />
            )}
        </PageCard>
    );
};

export default Projects;
