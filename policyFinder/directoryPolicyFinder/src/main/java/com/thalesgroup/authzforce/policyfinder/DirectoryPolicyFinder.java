package com.thalesgroup.authzforce.policyfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;
import com.sun.xacml.support.finder.PolicyCollection;
import com.sun.xacml.support.finder.PolicyReader;
import com.sun.xacml.support.finder.TopLevelPolicyException;

/**
 * This module is intended to look through a directory in order to load the
 * policies available.
 * 
 * @author romain.ferrari[AT]thalesgroup.com
 * @version 0.1
 * 
 */
public class DirectoryPolicyFinder extends PolicyFinderModule {

	// the schema file we're using, if any
	private File schemaFile = null;

	// the filenames for the files we'll load
	private Set fileNames;

	// the actual loaded policies
	private PolicyCollection policies;

	// the logger we'll use for all messages
	private static final Logger logger = Logger
			.getLogger(DirectoryPolicyFinder.class.getName());

	public DirectoryPolicyFinder() {
		this.fileNames = new TreeSet();
		this.policies = new PolicyCollection();

		String schemaName = System
				.getProperty(PolicyReader.POLICY_SCHEMA_PROPERTY);

		if (schemaName != null) {
			this.schemaFile = new File(schemaName);
		}
	}

	/**
	 * Constructor that specifies a set of initial policy files to use. This
	 * retrieves the schema file to validate policies against from the
	 * <code>PolicyReader.POLICY_SCHEMA_PROPERTY</code>. If the retrieved
	 * property is null, then no schema validation will occur.
	 * 
	 * @param fileNames
	 *            a <code>List</code> of <code>String</code>s that identify
	 *            policy files
	 */
	public DirectoryPolicyFinder(List fileNames) {
		this();
		for (int i = 0; i < fileNames.size(); i++) {
			logger.info("Loading from directory: " + fileNames.get(i));
			this.seekDirectory(fileNames.get(i).toString());
		}

		if (fileNames != null) {
			this.fileNames.addAll(fileNames);
		}
	}

	private void seekDirectory(String directoryPath) {
		File f = new File(directoryPath);
		String[] listPolicies = null;
		if (f.exists() && f.isDirectory()) {
			listPolicies = f.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.endsWith(".xml"));
				}
			});
		}
		for (int i = 0; i < listPolicies.length; i++) {
			if (listPolicies[i] != null) {				
//				logger.info("Loading Policy: " + directoryPath + listPolicies[i]);
				this.fileNames.add(directoryPath + listPolicies[i]);
			}
		}
	}

	/**
	 * Indicates whether this module supports finding policies based on a
	 * request (target matching). Since this module does support finding
	 * policies based on requests, it returns true.
	 * 
	 * @return true, since finding policies based on requests is supported
	 */
	public boolean isRequestSupported() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.xacml.finder.PolicyFinderModule#init(com.sun.xacml.finder.
	 * PolicyFinder)
	 */
	@Override
	public void init(PolicyFinder finder) {
		PolicyReader reader = new PolicyReader(this.schemaFile, finder);

		Iterator it = this.fileNames.iterator();
		while (it.hasNext()) {
			String fname = (String) (it.next());
			if (new File(fname).isFile()) {
				try {
					logger.info("Loading Policy: " + fname);
					AbstractPolicy policy = reader
							.readPolicy(new FileInputStream(fname));
					this.policies.addPolicy(policy);
				} catch (FileNotFoundException fnfe) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING, "File couldn't be read: "
								+ fname, fnfe);
					}
				} catch (ParsingException pe) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING,
								"Error reading policy from file " + fname, pe);
					}
				}
			}
		}
	}

	/**
	 * Finds a policy based on a request's context. If more than one applicable
	 * policy is found, this will return an error. Note that this is basically
	 * just a subset of the OnlyOneApplicable Policy Combining Alg that skips
	 * the evaluation step. See comments in there for details on this algorithm.
	 * 
	 * @param context
	 *            the representation of the request data
	 * 
	 * @return the result of trying to find an applicable policy
	 */
	public PolicyFinderResult findPolicy(EvaluationCtx context) {
		try {
			AbstractPolicy policy = this.policies.getPolicy(context);
			if (policy == null) {
				return new PolicyFinderResult();
			}
			return new PolicyFinderResult(policy);
		} catch (TopLevelPolicyException tlpe) {
			return new PolicyFinderResult(tlpe.getStatus());
		}
	}

}
