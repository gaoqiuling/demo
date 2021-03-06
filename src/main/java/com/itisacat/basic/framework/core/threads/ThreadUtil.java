package com.itisacat.basic.framework.core.threads;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class ThreadUtil {

	private static final String LOCAL_IP = getLocalIp();
	private static final String DEFAULT_LOCAL_IP = "127.0.0.1";
	private static volatile String ipGateway;
	private static volatile List<String> NETWORK_INTERFACE_NAMES;

	public static String getOneLocalIP() {
		return LOCAL_IP;
	}
	
	private ThreadUtil() {
		
	}

	private static String getLocalIp() {
		if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
			return getLocalHostLANAddress();
		} else {
			Enumeration<?> allNetInterfaces;
			try {
				allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			} catch (SocketException e) {
				log.warn(e.getMessage(), e);
				return DEFAULT_LOCAL_IP;
			}
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				for (InetAddress address : Collections.list(addresses)) {
					if (address instanceof Inet4Address) {
						String ip = address.getHostAddress();
						if (getIpGateway(ip)) {
							return ip;
						}
					}
				}
			}
		}

		return DEFAULT_LOCAL_IP;
	}


	private static String getLocalHostLANAddress() {
		if(NETWORK_INTERFACE_NAMES == null) {
			NETWORK_INTERFACE_NAMES = Lists.newArrayList("bond0", "eth0", "en0");
		}
		
		try {
			InetAddress candidateAddress = null;
			
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = ifaces.nextElement();
				if(!NETWORK_INTERFACE_NAMES.contains(iface.getDisplayName())) {
					continue;
				}
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							return inetAddr.getHostAddress();
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not
							// subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as
							// candidates,
							// only the first. For subsequent iterations, candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback
				// address.
				// Server might have a non-site-local address assigned to its NIC (or it might
				// be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress.getHostAddress();
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress.getHostAddress();
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			log.error(e.getMessage(), e);
		}
		return DEFAULT_LOCAL_IP;
	}

	private static boolean getIpGateway(String ip) {
		if (ipGateway == null) {
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			Process process = null;
			BufferedReader br = null;
			try {
//				if (SystemUtils.IS_OS_LINUX) {
//					String ipFlag = "default via";
//					process = Runtime.getRuntime().exec("/sbin/ip route list");
//					inputStream = process.getInputStream();
//					inputStreamReader = new InputStreamReader(inputStream);
//					br = new BufferedReader(inputStreamReader);
//					String result = br.lines().filter(line -> line.contains(ipFlag)).findFirst().orElse(null);
//
//					if (result != null) {
//						while (result.contains("  ")) {
//							result = result.replaceAll("  ", " ");
//						}
//						ipGateway = result.split(" ")[2];
//					}
//				} else 
//				if (SystemUtils.IS_OS_MAC) {
//					String ipFlag = "default";
//					process = Runtime.getRuntime().exec("netstat -rn");
//					inputStream = process.getInputStream();
//					inputStreamReader = new InputStreamReader(inputStream);
//					br = new BufferedReader(inputStreamReader);
//					String result = br.lines().filter(line -> line.contains(ipFlag)).findFirst().orElse(null);
//					if (result != null) {
//						while (result.contains("  ")) {
//							result = result.replaceAll("  ", " ");
//						}
//						ipGateway = result.split(" ")[1];
//					}
//
//				} else 
				if (SystemUtils.IS_OS_WINDOWS) {
					String ipFlag = "0.0.0.0";
					process = Runtime.getRuntime().exec("route print");
					inputStream = process.getInputStream();
					inputStreamReader = new InputStreamReader(inputStream);

					br = new BufferedReader(inputStreamReader);
					String result = br.lines().filter(line -> line.contains(ipFlag)).findFirst().orElse(null);
					if (result != null) {
						while (result.contains("  ")) {
							result = result.replaceAll("  ", " ");
						}
						ipGateway = result.split(" ")[3];
					}
				}
				if (ipGateway != null) {
					String[] bit = ipGateway.split("\\.");
					ipGateway = bit[0].concat(".").concat(bit[1]);

				}
			} catch (Exception e) {
				IOUtils.closeQuietly(br);
				IOUtils.closeQuietly(inputStreamReader);
				IOUtils.closeQuietly(inputStream);
				if (process != null) {
					process.destroyForcibly();
				}
			}
		}

		return ip.startsWith(ipGateway);
	}

}
