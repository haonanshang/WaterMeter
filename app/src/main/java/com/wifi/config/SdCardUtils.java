package com.wifi.config;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SdCardUtils
{

	// ����ֵ����File seperater "/",���û�����õڶ���sd��,����null
	public static String getSecondExternPath()
	{
		List<String> paths = getAllExterSdcardPath();

		if (paths.size() == 2)
		{

			for (String path : paths)
			{
				if (path != null && !path.equals(getFirstExternPath()))
				{
					return path;
				}
			}

			return null;

		} else
		{
			return null;
		}
	}

	public static boolean isFirstSdcardMounted()
	{
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			return false;
		}
		return true;
	}

	public static boolean isSecondSDcardMounted()
	{
		String sd2 = getSecondExternPath();
		if (sd2 == null)
		{
			return false;
		}
		return checkFsWritable(sd2 + File.separator);

	}

	// ��������sd���Ƿ�ж�أ�����ֱ���ж�����sd���Ƿ�Ϊnull����Ϊ������sd���γ�ʱ����Ȼ�ܵõ�����sd��·���������ַ����ǰ���android�ȸ����DICM�ķ�����
	// ����һ���ļ���Ȼ������ɾ�������Ƿ�ж������sd��
	// ע��������һ��Сbug����ʹ����sd��û��ж�أ����Ǵ洢�ռ䲻���󣬻����ļ����������������ʱ��Ҳ���ܴ������ļ�����ʱ��ͳһ��ʾ�û�����sd����
	private static boolean checkFsWritable(String dir)
	{

		if (dir == null)
			return false;

		File directory = new File(dir);

		if (!directory.isDirectory())
		{
			if (!directory.mkdirs())
			{
				return false;
			}
		}

		File f = new File(directory, ".keysharetestgzc");
		try
		{
			if (f.exists())
			{
				f.delete();
			}
			if (!f.createNewFile())
			{
				return false;
			}
			f.delete();
			return true;

		} catch (Exception e)
		{
		}
		return false;

	}

	public static String getFirstExternPath()
	{
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static List<String> getAllExterSdcardPath()
	{
		List<String> SdList = new ArrayList<String>();

		String firstPath = getFirstExternPath();

		// �õ�·��
		try
		{
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null)
			{
				// ��������linux�������˵�
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				if (line.contains("media"))
					continue;
				if (line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data") || line.contains("tmpfs") || line.contains("shell") || line.contains("root")
						|| line.contains("acct") || line.contains("proc") || line.contains("misc") || line.contains("obb"))
				{
					continue;
				}

				if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs")))
				{

					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1)
					{
						String path = columns[1].toLowerCase(Locale.getDefault());
						if (path != null && !SdList.contains(path) && path.contains("sd"))
							SdList.add(columns[1]);
					}
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!SdList.contains(firstPath))
		{
			SdList.add(firstPath);
		}

		return SdList;
	}
}
