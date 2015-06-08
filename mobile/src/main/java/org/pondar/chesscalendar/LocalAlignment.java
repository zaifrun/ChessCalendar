package org.pondar.chesscalendar;

public class LocalAlignment {
	String s1,s2;
	int gapScore = -2;
	int match = 3;
	int mismatch = -1;
	float percentage;
	int maxScore;
	
	public LocalAlignment(String s1,String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public LocalAlignment()
	{
		
	}
	
	private int max4(int i1,int i2,int i3,int i4)
	{
		int max = i1;
		if (i2>max)
			max = i2;
		if (i3>max)
			max = i3;
		if (i4>max)
			max = i4;
		return max;
	}
	
	
	public void run(String s1,String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
		run();
	}
	
	public void run()
	{
		int m = s1.length()+1;
		int n = s2.length()+1;
		int[][] matrix = new int[m][n];
		for (int i = 0; i<m; i++)
			matrix[i][0] = 0;
		for (int i = 0; i<n; i++)
			matrix[0][i] = 0;
		
		maxScore = 0;
		for (int i = 1; i<m; i++)
		{
			for (int j = 1; j<n; j++)
			{
				int p = match; //assume match
				if (s1.charAt(i-1)!=s2.charAt(j-1))
					p = mismatch;
				
				int max = max4(matrix[i][j-1]+gapScore,
								matrix[i-1][j-1]+p,
								matrix[i-1][j]+gapScore,
								0);	
				matrix[i][j] = max;
				if (max>maxScore)
					maxScore = max;
				
			}
		}
		/*
		for (int i = 0; i<m; i++)
		{
			for (int j = 0; j<n; j++)
			{
				System.out.print(" "+matrix[i][j]);
			}
			System.out.println();
		}*/
		percentage =(float) maxScore /(float) (match*s1.length());
	//	System.out.println("max score = "+maxScore);
	//	System.out.println("percentage = "+percentage);
		
	}
	
	public float getPercentage()
	{
		return percentage;
	}
	
	public int getMaxScore()
	{
		return maxScore;
	}
	
	public void setStrings(String s1,String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
	}

}
