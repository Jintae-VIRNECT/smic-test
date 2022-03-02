package com.virnect.smic.daemon.config.support;

public class DefaultJobLoader {//implements JobLoader {

	//private JobRegistry jobRegistry;

	// private TaskRegistry taskRegistry;
	//
	// private Map<ApplicationContextFactory, ConfigurableApplicationContext> contexts = new ConcurrentHashMap<>();
	//
	// private Map<ConfigurableApplicationContext, Collection<String>> contextToJobNames = new ConcurrentHashMap<>();
	//
	// @Override
	// public void load(ApplicationContextFactory factory) {
	//
	// }
	//
	// @Override
	// public void reload(ApplicationContextFactory factory) {
	//
	// }
	//
	// @Override
	// public void clear() {
	//
	// }
	//
	// private Collection<Job>  doLoad(ApplicationContextFactory factory, boolean unregister)  {
	//
	//
	// 	ConfigurableApplicationContext context = factory.createApplicationContext();
	// 	contexts.put(factory, context);
	// 	String[] names = context.getBeanNamesForType(Job.class);
	// 	Collection<String> jobsRegistered = new HashSet<>();
	// 	for (String name : names) {
	//
	// 		Job job = (Job) context.getBean(name);
	// 		String jobName = job.getName();
	//
	// 		doRegister(context, job);
	// 		jobsRegistered.add(jobName);
	// 	}
	//
	// 	Collection<Job> result = new ArrayList<>();
	// 	for (String name : jobsRegistered) {
	// 		try {
	// 			result.add(jobRegistry.getJob(name));
	// 		}
	// 		catch (NoSuchJobException e) {
	// 			throw new IllegalStateException("Could not retrieve job that was should have been registered", e);
	// 		}
	//
	// 	}
	//
	// 	contextToJobNames.put(context, jobsRegistered);
	//
	// 	return result;
	// }
	//
	// private void doRegister(ConfigurableApplicationContext context, Job job) {
	// 	final JobFactory jobFactory = new ReferenceJobFactory(job);
	// 	jobRegistry.register(jobFactory);
	//
	// 	if (taskRegistry != null) {
	// 		if (!(job instanceof TaskLocator)) {
	// 			throw new UnsupportedOperationException("Cannot locate steps from a Job that is not a StepLocator: job="
	// 				+ job.getName() + " does not implement StepLocator");
	// 		}
	// 		taskRegistry.register(job.getName(), getTasks((TaskLocator) job, context));
	// 	}
	// }
	//
	// private Collection<Task> getTasks(final TaskLocator taskLocator, final ApplicationContext jobApplicationContext) {
	//
	// 	final Collection<Task> result = new ArrayList<>();
	// 	final Collection<String> taskNames = taskLocator.getTaskNames();
	// 	taskNames.forEach(taskName -> {result.add(taskLocator.getTask(taskName));});
	// 	return result;
	// }
}
